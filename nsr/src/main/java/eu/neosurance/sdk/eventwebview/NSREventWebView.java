package eu.neosurance.sdk.eventwebview;

import android.app.PendingIntent;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Locale;

import eu.neosurance.sdk.NSRAuth;
import eu.neosurance.sdk.NSRNotification;
import eu.neosurance.sdk.data.DataManager;
import eu.neosurance.sdk.processors.ProcessorManager;
import eu.neosurance.sdk.processors.request.RequestProcessor;
import eu.neosurance.sdk.webview.ActivityWebViewManager;
import eu.neosurance.sdk.utils.DeviceUtils;

public class NSREventWebView {

    private static final String TAG = NSREventWebView.class.getCanonicalName();
    
    private DataManager dataManager;
    private ProcessorManager processorManager;
    private WebView webView = null;
    private Context ctx;
    private ActivityWebViewManager activityWebViewManager;

    public NSREventWebView(Context ctx,
                           ProcessorManager processorManager,
                           DataManager dataManager,
                           ActivityWebViewManager activityWebViewManager) {
        try {
            this.ctx = ctx;
            this.processorManager = processorManager;
            this.dataManager = dataManager;
            this.activityWebViewManager = activityWebViewManager;

            webView = new WebView(ctx);
            webView.addJavascriptInterface(this, "NSR");
            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setAllowFileAccessFromFileURLs(true);
            webView.getSettings().setAllowUniversalAccessFromFileURLs(true);
            webView.getSettings().setDomStorageEnabled(true);
            webView.loadUrl("file:///android_asset/eventCrucher.html");
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    public void synch() {
        eval("synch()");
    }

    public void crunchEvent(final String event, final JSONObject payload) {
        try {
            JSONObject nsrEvent = new JSONObject();
            nsrEvent.put("event", event);
            nsrEvent.put("payload", payload);
            eval("crunchEvent(" + nsrEvent.toString() + ")");
        } catch (JSONException e) {
            Log.e(TAG, "crunchEvent", e);
        }
    }

    protected void eval(final String code) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            public void run() {
                webView.evaluateJavascript(code, null);
            }
        });
    }

    @JavascriptInterface
    public void postMessage(final String json) {
        try {
            final JSONObject body = new JSONObject(json);
            if (body.has("log")) {
                Log.i(TAG, body.getString("log"));
            }
            if (body.has("event") && body.has("payload")) {
                processorManager.getEventProcessor().sendEvent(body.getString("event"), body.getJSONObject("payload"));
            }
            if (body.has("action")) {
                processorManager.getActionProcessor().sendAction(body.getString("action"), body.getString("code"), body.getString("details"));
            }
            if (body.has("what")) {
                if ("init".equals(body.getString("what")) && body.has("callBack")) {
                    processorManager.getAuthProcessor().authorize(new NSRAuth() {
                        public void authorized(boolean authorized) throws Exception {
                            if (authorized) {
                                JSONObject message = new JSONObject();
                                message.put("api", dataManager.getSettingsRepository().getSettings().getString("base_url"));
                                message.put("token", dataManager.getAuthRepository().getToken());
                                message.put("lang", dataManager.getSettingsRepository().getLang());
                                message.put("deviceUid", DeviceUtils.getDeviceUid(ctx));
                                eval(body.getString("callBack") + "(" + message.toString() + ")");
                            }
                        }
                    });
                }
                if ("token".equals(body.getString("what")) && body.has("callBack")) {
                    processorManager.getAuthProcessor().authorize(new NSRAuth() {
                        public void authorized(boolean authorized) throws Exception {
                            if (authorized) {
                                eval(body.getString("callBack") + "('" + dataManager.getAuthRepository().getToken() + "')");
                            }
                        }
                    });
                }
                if ("user".equals(body.getString("what")) && body.has("callBack")) {
                    eval(body.getString("callBack") + "(" + dataManager.getUserRepository().getUser().toJsonObject(true).toString() + ")");
                }
                if ("push".equals(body.getString("what")) && body.has("title") && body.has("body")) {
                    String imageUrl = body.has("imageUrl") ? body.getString("imageUrl") : null;
                    String url = body.has("url") ? body.getString("url") : null;

                    PendingIntent pendingIntent;
                    if (url != null && !"".equals(url))
                        pendingIntent = PendingIntent.getActivity(ctx, (int) System.currentTimeMillis(), activityWebViewManager.makeActivityWebView(ctx, url), PendingIntent.FLAG_UPDATE_CURRENT);
                    else pendingIntent = null;

                    NSRNotification.sendNotification(ctx, body.getString("title"), body.getString("body"), imageUrl, pendingIntent);
                }
                if ("geoCode".equals(body.getString("what")) && body.has("location") && body.has("callBack")) {
                    Geocoder geocoder = new Geocoder(ctx, Locale.forLanguageTag(dataManager.getSettingsRepository().getLang()));
                    JSONObject location = body.getJSONObject("location");
                    List<Address> addresses = geocoder.getFromLocation(location.getDouble("latitude"), location.getDouble("longitude"), 1);
                    if (addresses != null && addresses.size() > 0) {
                        Address adr = addresses.get(0);
                        JSONObject address = new JSONObject();
                        address.put("countryCode", adr.getCountryCode().toUpperCase());
                        address.put("countryName", adr.getCountryName());
                        String adrLine = adr.getAddressLine(0);
                        address.put("address", adrLine != null ? adrLine : "");
                        eval(body.getString("callBack") + "(" + address.toString() + ")");
                    }
                }
                if ("callApi".equals(body.getString("what")) && body.has("callBack")) {
                    processorManager.getAuthProcessor().authorize(new NSRAuth() {
                        public void authorized(boolean authorized) throws Exception {
                            if (!authorized) {
                                JSONObject result = new JSONObject();
                                result.put("status", "error");
                                result.put("message", "not authorized");
                                eval(body.getString("callBack") + "(" + result.toString() + ")");
                                return;
                            }
                            JSONObject headers = new JSONObject();
                            headers.put("ns_token", dataManager.getAuthRepository().getToken());
                            headers.put("ns_lang", dataManager.getSettingsRepository().getLang());
                            processorManager.getRequestProcessor().performSecureRequest(body.getString("endpoint"), body.has("payload") ? body.getJSONObject("payload") : null, headers, new RequestProcessor.RequestListener() {
                                @Override
                                public void onRequestSuccess(JSONObject json) throws Exception {
                                    eval(body.getString("callBack") + "(" + json.toString() + ")");
                                }

                                @Override
                                public void onRequestFailure(String error) throws JSONException {
                                    Log.e(TAG, "secureRequest: " + error);
                                    JSONObject result = new JSONObject();
                                    result.put("status", "error");
                                    result.put("message", error);
                                    eval(body.getString("callBack") + "(" + result.toString() + ")");
                                }
                            });

                        }
                    });
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "postMessage", e);
        }
    }
}
