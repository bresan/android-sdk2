package eu.neosurance.sdk;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Location;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import eu.neosurance.sdk.utils.DeviceUtils;

public class NSRActivityWebView extends AppCompatActivity {
	private WebView webView;
	private String photoCallback;
	private NSR nsr;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		nsr = NSR.getInstance(getApplicationContext());
		nsr.getActivityWebViewManager().registerWebView(this);
		try {
			String url = getIntent().getExtras().getString("url");
			webView = new WebView(this);
			webView.addJavascriptInterface(this, "NSSdk");
			webView.getSettings().setJavaScriptEnabled(true);
			webView.getSettings().setAllowFileAccessFromFileURLs(true);
			webView.getSettings().setAllowUniversalAccessFromFileURLs(true);
			webView.getSettings().setDomStorageEnabled(true);
			webView.setWebViewClient(new WebViewClient() {
				public boolean shouldOverrideUrlLoading(WebView view, String url) {
					if (url.endsWith(".pdf")) {
						Intent intent = new Intent(Intent.ACTION_VIEW);
						intent.setDataAndType(Uri.parse(url), "application/pdf");
						intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
						startActivity(intent);
						return true;
					} else {
						return false;
					}
				}
			});
			setContentView(webView);
			webView.loadUrl(url);
			idle();
		} catch (Exception e) {
			Log.e(NSR.TAG, e.getMessage(), e);
		}
	}

	public synchronized void navigate(final String url) {
		new Handler(Looper.getMainLooper()).post(new Runnable() {
			public void run() {
				try {
					if (webView != null) {
						webView.loadUrl(url);
					}
				} catch (Throwable e) {
				}
			}
		});
	}

	protected void eval(final String code) {
		new Handler(Looper.getMainLooper()).post(new Runnable() {
			public void run() {
				try {
					if (webView != null) {
						webView.evaluateJavascript(code, null);
					}
				} catch (Throwable e) {
				}
			}
		});
	}

	@JavascriptInterface
	public void postMessage(final String json) {
		try {
			final JSONObject body = new JSONObject(json);
			if (body.has("log")) {
				Log.i(NSR.TAG, body.getString("log"));
			}
			if (body.has("event") && body.has("payload")) {
				nsr.getProcessorManager().getEventProcessor().sendEvent(body.getString("event"), body.getJSONObject("payload"));
			}
			if (body.has("crunchEvent") && body.has("payload")) {
				nsr.getProcessorManager().getEventProcessor().crunchEvent(body.getString("event"), body.getJSONObject("payload"));
			}
			if (body.has("action")) {
				nsr.getProcessorManager().getActionProcessor().sendAction(body.getString("action"), body.getString("code"), body.getString("details"));
			}
			if (body.has("what")) {
				if ("init".equals(body.getString("what")) && body.has("callBack")) {
					nsr.getProcessorManager().getAuthProcessor().authorize(new NSRAuth() {
						public void authorized(boolean authorized) throws Exception {
							JSONObject settings = nsr.getSettingsRepository().getSettings();
							JSONObject message = new JSONObject();
							message.put("api", settings.getString("base_url"));
							message.put("token", nsr.getAuthRepository().getToken());
							message.put("lang", nsr.getSettingsRepository().getLang());
							message.put("deviceUid", DeviceUtils.getDeviceUid(NSRActivityWebView.this));
							eval(body.getString("callBack") + "(" + message.toString() + ")");
						}
					});
				}
				if ("close".equals(body.getString("what"))) {
					finish();
				}
				if ("photo".equals(body.getString("what")) && body.has("callBack")) {
					takePhoto(body.getString("callBack"));
				}
				if ("location".equals(body.getString("what")) && body.has("callBack")) {
					getLocation(body.getString("callBack"));
				}
				if ("user".equals(body.getString("what")) && body.has("callBack")) {
					eval(body.getString("callBack") + "(" + nsr.getDataManager().getUserRepository().getUser().toJsonObject(true).toString() + ")");
				}
				if ("showApp".equals(body.getString("what"))) {
					if (body.has("params")) {
						nsr.showApp(body.getJSONObject("params"));
					} else {
						nsr.showApp();
					}
				}
				if ("showUrl".equals(body.getString("what")) && body.has("url")) {
					if (body.has("params")) {
						nsr.getActivityWebViewManager().showUrl(body.getString("url"), body.getJSONObject("params"));
					} else {
						nsr.getActivityWebViewManager().showUrl(body.getString("url"));
					}
				}
				if ("callApi".equals(body.getString("what")) && body.has("callBack")) {
					nsr.getProcessorManager().getAuthProcessor().authorize(new NSRAuth() {
						public void authorized(boolean authorized) throws Exception {
							if (!authorized) {
								JSONObject result = new JSONObject();
								result.put("status", "error");
								result.put("message", "not authorized");
								eval(body.getString("callBack") + "(" + result.toString() + ")");
								return;
							}
							JSONObject headers = new JSONObject();
							headers.put("ns_token", nsr.getAuthRepository().getToken());
							headers.put("ns_lang", nsr.getSettingsRepository().getLang());
							nsr.getSecurityDelegate().secureRequest(getApplicationContext(), body.getString("endpoint"), body.has("payload") ? body.getJSONObject("payload") : null, headers, new NSRSecurityResponse() {
								public void completionHandler(JSONObject json, String error) throws Exception {
									if (error == null) {
										eval(body.getString("callBack") + "(" + json.toString() + ")");
									} else {
										Log.e(NSR.TAG, "secureRequest: " + error);
										JSONObject result = new JSONObject();
										result.put("status", "error");
										result.put("message", error);
										eval(body.getString("callBack") + "(" + result.toString() + ")");
									}
								}
							});
						}
					});
				}
				if (nsr.getWorkflowDelegate() != null && "executeLogin".equals(body.getString("what")) && body.has("callBack")) {
					new Handler(Looper.getMainLooper()).post(new Runnable() {
						public void run() {
							try {
								webView.evaluateJavascript(body.getString("callBack") + "(" + nsr.getWorkflowDelegate().executeLogin(getApplicationContext(), webView.getUrl()) + ")",null);
							} catch (Throwable e) {
							}
						}
					});
				}
				if (nsr.getWorkflowDelegate() != null && "executePayment".equals(body.getString("what")) && body.has("payment")) {
					new Handler(Looper.getMainLooper()).post(new Runnable() {
						public void run() {
							try {
								JSONObject paymentInfo = nsr.getWorkflowDelegate().executePayment(getApplicationContext(), body.getJSONObject("payment"), webView.getUrl());
								if (body.has("callBack")) {
									webView.evaluateJavascript(body.getString("callBack") + "(" + (paymentInfo != null ? paymentInfo.toString() : "") + ")",null);
								}
							} catch (Throwable e) {
							}
						}
					});
				}
			}
		} catch (Exception e) {
			Log.e(NSR.TAG, "postMessage", e);
		}
	}

	private File imageFile() {
		File path = new File(Environment.getExternalStorageDirectory(), this.getPackageName());
		if (!path.exists()) {
			path.mkdir();
		}
		return new File(path, "nsr-photo.jpg");
	}

	private void takePhoto(final String callBack) {
		boolean camera = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
		boolean storage = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
		if (camera && storage) {
			photoCallback = callBack;
			Intent mIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			if (mIntent.resolveActivity(this.getPackageManager()) != null) {
				mIntent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(this, getPackageName() + ".provider", imageFile()));
				this.startActivityForResult(mIntent, Constants.REQUEST_IMAGE_CAPTURE);
			}
		} else {
			List<String> permissionsList = new ArrayList<String>();
			if (!camera) {
				permissionsList.add(Manifest.permission.CAMERA);
			}
			if (!storage) {
				permissionsList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
			}
			ActivityCompat.requestPermissions(this, permissionsList.toArray(new String[permissionsList.size()]), Constants.PERMISSIONS_MULTIPLE_IMAGECAPTURE);
		}
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == Constants.REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
			try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
				int orientation = new ExifInterface(imageFile().getAbsolutePath()).getAttributeInt(ExifInterface.TAG_ORIENTATION, 0);
				int degree = 0;
				if (orientation == 6) {
					degree = 90;
				} else if (orientation == 3) {
					degree = 180;
				} else if (orientation == 8) {
					degree = 270;
				}
				Bitmap b = BitmapFactory.decodeFile(imageFile().getAbsolutePath());
				if (degree > 0) {
					Matrix matrix = new Matrix();
					matrix.postRotate(90);
					b = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), matrix, true);
				}
				float k = 1;
				int maxSize = (b.getWidth() >= b.getHeight()) ? b.getWidth() : b.getHeight();
				if (maxSize > 1024) {
					k = (1024F / maxSize);
				}
				Bitmap.createScaledBitmap(b, Math.round(b.getWidth() * k), Math.round(b.getHeight() * k), false).compress(Bitmap.CompressFormat.JPEG, 60, baos);
				imageFile().delete();
				eval(photoCallback + "('data:image/jpeg;base64," + Base64.encodeToString(baos.toByteArray(), Base64.NO_WRAP) + "')");
			} catch (Exception e) {
				Log.d(NSR.TAG, e.getMessage(), e);
			}
		}
	}

	@SuppressLint("MissingPermission")
	private void getLocation(final String callBack) {
		boolean coarse = (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED);
		boolean fine = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
		if (coarse && fine) {
			final FusedLocationProviderClient locationClient = LocationServices.getFusedLocationProviderClient(this);
			LocationRequest locationRequest = LocationRequest.create();
			locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
			locationRequest.setInterval(0);
			locationRequest.setFastestInterval(0);
			locationRequest.setNumUpdates(1);
			locationClient.requestLocationUpdates(locationRequest,
				new LocationCallback() {
					public void onLocationResult(LocationResult locationResult) {
						Location location = locationResult.getLastLocation();
						if (location != null) {
							locationClient.removeLocationUpdates(this);
							try {
								JSONObject locationAsJson = new JSONObject();
								locationAsJson.put("latitude", location.getLatitude());
								locationAsJson.put("longitude", location.getLongitude());
								eval(callBack + "(" + locationAsJson.toString() + ")");
							} catch (JSONException e) {
							}
						}
					}
				}, null);
		} else {
			List<String> permissionsList = new ArrayList<String>();
			if (!fine) {
				permissionsList.add(Manifest.permission.ACCESS_FINE_LOCATION);
			}
			if (!coarse) {
				permissionsList.add(Manifest.permission.ACCESS_COARSE_LOCATION);
			}
			ActivityCompat.requestPermissions(this, permissionsList.toArray(new String[permissionsList.size()]), Constants.PERMISSIONS_MULTIPLE_ACCESSLOCATION);
		}
	}

	private void idle() {
		new Handler().postDelayed(new Runnable() {
			public void run() {
				try {
					if (webView != null) {
						webView.evaluateJavascript("(function() { return (window.document.body.className.indexOf('NSR') == -1 ? false : true); })();", new ValueCallback<String>() {
							public void onReceiveValue(String value) {
								if ("true".equals(value)) {
									idle();
								} else {
									finish();
								}
							}
						});
					}
				} catch (Throwable e) {
				}
			}
		}, 15 * 1000);
	}

	public synchronized void finish() {
		nsr.getActivityWebViewManager().clearWebView();
		new Handler(Looper.getMainLooper()).post(new Runnable() {
			public void run() {
				try {
					if (webView != null) {
						webView.stopLoading();
						webView.destroy();
						webView = null;
					}
				} catch (Throwable e) {
				}
			}
		});
		super.finish();
	}

}
