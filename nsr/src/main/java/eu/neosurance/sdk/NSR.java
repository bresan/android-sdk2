package eu.neosurance.sdk;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.webkit.WebView;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import eu.neosurance.sdk.auth.AuthArguments;
import eu.neosurance.sdk.auth.AuthListener;
import eu.neosurance.sdk.auth.AuthManager;
import eu.neosurance.sdk.platform.persistence.PersistenceFactory;
import eu.neosurance.sdk.platform.persistence.PersistenceManager;
import eu.neosurance.sdk.tracer.TracerFactory;
import eu.neosurance.sdk.tracer.TracerListener;
import eu.neosurance.sdk.tracer.activity.ActivityTracer;
import eu.neosurance.sdk.tracer.connection.ConnectionTracer;
import eu.neosurance.sdk.tracer.location.LocationTracer;
import eu.neosurance.sdk.tracer.power.PowerTracer;
import eu.neosurance.sdk.utils.DeviceUtils;

public class NSR implements TracerListener, AuthListener {

    public static boolean isInvalidAndroidVersion = android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.LOLLIPOP;

    protected String getOs() {
        return "Android";
    }

    protected String getVersion() {
        return "2.0.3";
    }

    protected static final String PREFS_NAME = "NSRSDK";
    protected static final String TAG = "nsr";
    protected static final String JOB_TAG = "nsrJob";
    protected static final int PERMISSIONS_MULTIPLE_ACCESSLOCATION = 0x2043;
    protected static final int PERMISSIONS_MULTIPLE_IMAGECAPTURE = 0x2049;
    protected static final int REQUEST_IMAGE_CAPTURE = 0x1702;

    private static NSR instance = null;
    private Context context = null;
    private NSREventWebView eventWebView = null;
    private long eventWebViewSynchTime = 0;

    private NSRSecurityDelegate securityDelegate = null;
    private NSRWorkflowDelegate workflowDelegate = null;
    private NSRActivityWebView activityWebView = null;


    // Tracer related objects
    private TracerFactory tracerFactory;
    private PowerTracer powerTracer;
    private LocationTracer locationTracer;
    private ActivityTracer activityTracer;
    private ConnectionTracer connectionTracer;

    // Manager objects
    private AuthManager authManager;
    private PersistenceManager persistenceManager;


    private NSR(Context context) {
        this.context = context;

        initTracers();
        initManagers();
    }

    private void initManagers() {
        persistenceManager = new PersistenceFactory(context).makePersistenceManager();
    }

    private void initTracers() {
        tracerFactory = new TracerFactory(context, this);
        powerTracer = tracerFactory.makePowerTracer();
        locationTracer = tracerFactory.makeLocationTracer();
        activityTracer = tracerFactory.makeActivityTracer();
        connectionTracer = tracerFactory.makeConnectionTracer();
    }

    public static NSR getInstance(Context ctx) {
        if (instance == null) {
            Log.d(TAG, "making instance...");
            instance = new NSR(ctx);
            if (!isInvalidAndroidVersion) {
                try {
                    String s = instance.getPersistenceManager().retrieveData("securityDelegateClass");
                    if (s != null) {
                        Log.d(TAG, "making securityDelegate... " + s);
                        instance.setSecurityDelegate((NSRSecurityDelegate) Class.forName(s).newInstance());
                    } else {
                        Log.d(TAG, "making securityDelegate... NSRDefaultSecurity");
                        instance.setSecurityDelegate(new NSRDefaultSecurity());
                    }

                    s = instance.getPersistenceManager().retrieveData("workflowDelegateClass");
                    if (s != null) {
                        Log.d(TAG, "making workflowDelegate... " + s);
                        instance.setWorkflowDelegate((NSRWorkflowDelegate) Class.forName(s).newInstance());
                    }

                    instance.initJob();
                } catch (Exception e) {
                    Log.e(TAG, "getInstance", e);
                }
            }
        } else {
            instance.context = ctx;
        }
        return instance;
    }

    @Override
    public void initJob() {
        Log.d(TAG, "initJob");
        try {
            stopTraceActivity();
            stopTraceLocation();
            new FirebaseJobDispatcher(new GooglePlayDriver(context)).cancel(JOB_TAG);
            JSONObject conf = getConf();
            if (conf != null) {
                if (eventWebView == null && conf.has("local_tracking") && conf.getBoolean("local_tracking")) {
                    Log.d(TAG, "Making NSREventWebView");
                    eventWebView = new NSREventWebView(context, this);
                    eventWebViewSynchTime = System.currentTimeMillis() / 1000;
                }

                int time = conf.getInt("time");
                FirebaseJobDispatcher jobDispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(context));
                Job myJob = jobDispatcher.newJobBuilder()
                        .setService(NSRJobService.class)
                        .setTag(JOB_TAG)
                        .setRecurring(true)
                        .setTrigger(Trigger.executionWindow(time / 2, time))
                        .setLifetime(Lifetime.FOREVER)
                        .setReplaceCurrent(true)
                        .setConstraints(Constraint.ON_ANY_NETWORK)
                        .setRetryStrategy(RetryStrategy.DEFAULT_LINEAR)
                        .build();
                jobDispatcher.mustSchedule(myJob);
            }
        } catch (Exception e) {
            Log.e(TAG, "initJob", e);
        }
    }

    @Override
    public void synchEventWebView() {
        long t = System.currentTimeMillis() / 1000;
        if (eventWebView != null && t - eventWebViewSynchTime > (60 * 60 * 8)) {
            eventWebView.synch();
            eventWebViewSynchTime = t;
        }
    }

    @Override
    public boolean needsInitJob(JSONObject conf, JSONObject oldConf) throws Exception {
        return (oldConf == null) ||
                (conf.getInt("time") != oldConf.getInt("time")) ||
                (eventWebView == null && conf.has("local_tracking") &&
                        conf.getBoolean("local_tracking"));
    }

    /**
     * Location Related Events
     */
    protected void traceLocation() {
        locationTracer.trace(getConf());
    }

    protected void stopTraceLocation() {
        locationTracer.stopTrace();
    }

    protected Location getLastLocation() {
        return locationTracer.getLastLocation();
    }

    protected void setLastLocation(Location lastLocation) {
        this.locationTracer.setLastLocation(lastLocation);
    }

    protected boolean getStillLocation() {
        return this.locationTracer.getStillLocation();
    }

    protected void setStillLocation(boolean stillLocation) {
        this.locationTracer.setStillLocation(stillLocation);
    }

    /**
     * Power related events
     */
    protected void tracePower() {
        powerTracer.trace(getConf());
    }

    /**
     * Activity related events
     */
    protected void traceActivity() {
        activityTracer.trace(getConf());
    }

    protected void stopTraceActivity() {
        activityTracer.stopTrace();
    }

    protected String getLastActivity() {
        return activityTracer.getLastActivity();
    }

    protected void setLastActivity(String lastActivity) {
        this.activityTracer.setLastActivity(lastActivity);
    }

    /**
     * Connection related events
     */
    protected void traceConnection() {
        this.connectionTracer.trace(getConf());
    }

    protected void registerWebView(NSRActivityWebView activityWebView) {
        if (this.activityWebView != null)
            this.activityWebView.finish();
        this.activityWebView = activityWebView;
    }

    protected void clearWebView() {
        this.activityWebView = null;
    }

    protected NSRSecurityDelegate getSecurityDelegate() {
        return securityDelegate;
    }

    public void setSecurityDelegate(NSRSecurityDelegate securityDelegate) {
        if (isInvalidAndroidVersion) {
            return;
        }
        getPersistenceManager().storeData("securityDelegateClass", securityDelegate.getClass().getName());
        this.securityDelegate = securityDelegate;

        authManager = new AuthManager(context, securityDelegate, this);
    }

    protected NSRWorkflowDelegate getWorkflowDelegate() {
        return workflowDelegate;
    }

    public void setWorkflowDelegate(NSRWorkflowDelegate workflowDelegate) {
        if (isInvalidAndroidVersion) {
            return;
        }
        getPersistenceManager().storeData("workflowDelegateClass", workflowDelegate.getClass().getName());
        this.workflowDelegate = workflowDelegate;
    }

    public void setup(final JSONObject settings) {
        if (isInvalidAndroidVersion) {
            return;
        }
        Log.d(TAG, "setup");
        try {
            if (!settings.has("ns_lang")) {
                settings.put("ns_lang", Locale.getDefault().getLanguage());
            }
            if (!settings.has("dev_mode")) {
                settings.put("dev_mode", 0);
            }
            WebView.setWebContentsDebuggingEnabled(settings.getInt("dev_mode") != 0);

            if (!settings.has("push_icon")) {
                settings.put("push_icon", R.drawable.nsr_logo);
            }
            Log.d(TAG, "setup: " + settings);
            setSettings(settings);
            if (getPersistenceManager().retrieveData("permission_requested") == null && settings.has("ask_permission") && settings.getInt("ask_permission") == 1) {
                getPersistenceManager().storeData("permission_requested", "*");
                List<String> permissionsList = new ArrayList<String>();
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    permissionsList.add(Manifest.permission.ACCESS_FINE_LOCATION);
                }
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    permissionsList.add(Manifest.permission.ACCESS_COARSE_LOCATION);
                }
                if (permissionsList.size() > 0) {
                    ActivityCompat.requestPermissions((Activity) context, permissionsList.toArray(new String[permissionsList.size()]), NSR.PERMISSIONS_MULTIPLE_ACCESSLOCATION);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "setup", e);
        }
    }

    public void registerUser(NSRUser user) {
        if (isInvalidAndroidVersion) {
            return;
        }
        Log.d(TAG, "registerUser");
        try {
            forgetUser();
            setUser(user);
            authManager.authorize(new NSRAuth() {
                public void authorized(boolean authorized) throws Exception {
                    Log.d(TAG, "registerUser: " + (authorized ? "" : "not ") + "authorized!");
                }
            }, makeAuthArguments());
        } catch (Exception e) {
            Log.e(TAG, "registerUser", e);
        }
    }

    public void forgetUser() {
        if (isInvalidAndroidVersion) {
            return;
        }
        Log.d(TAG, "forgetUser");
        setConf(null);
        setAuth(null);
        setAppURL(null);
        setUser(null);
        initJob();
    }

    private AuthArguments makeAuthArguments() {
        AuthArguments authArguments = new AuthArguments.Builder()
                .conf(getConf())
                .auth(getAuth())
                .os(getOs())
                .version(getVersion())
                .settings(getSettings())
                .user(getUser())
                .build();

        return authArguments;
    }


    public void sendAction(final String name, final String policyCode, final String details) {
        if (isInvalidAndroidVersion) {
            return;
        }
        Log.d(TAG, "sendAction - name: " + name + " policyCode: " + policyCode + " details: " + details);
        try {
            authManager.authorize(new NSRAuth() {
                public void authorized(boolean authorized) throws Exception {
                    JSONObject requestPayload = new JSONObject();

                    requestPayload.put("action", name);
                    requestPayload.put("code", policyCode);
                    requestPayload.put("details", details);
                    requestPayload.put("timezone", TimeZone.getDefault().getID());
                    requestPayload.put("action_time", System.currentTimeMillis());

                    JSONObject headers = new JSONObject();
                    String token = getToken();
                    Log.d(TAG, "sendAction token: " + token);
                    headers.put("ns_token", token);
                    headers.put("ns_lang", getLang());

                    getSecurityDelegate().secureRequest(context, "trace", requestPayload, headers, new NSRSecurityResponse() {
                        public void completionHandler(JSONObject json, String error) throws Exception {
                            if (error != null) {
                                Log.e(TAG, "sendAction: " + error);
                            } else {
                                Log.d(TAG, "sendAction: " + json.toString());
                            }
                        }
                    });
                }
            }, makeAuthArguments());
        } catch (Exception e) {
            Log.e(TAG, "sendAction", e);
        }
    }

    protected void crunchEvent(final String event, final JSONObject payload) throws Exception {
        JSONObject conf = getConf();
        if (conf != null && conf.has("local_tracking") && conf.getBoolean("local_tracking")) {
            Log.d(NSR.TAG, "crunchEvent: " + event + " payload: " + payload.toString());
            if (eventWebView != null) {
                eventWebView.crunchEvent(event, payload);
            }
        } else {
            sendEvent(event, payload);
        }
    }

    public void sendEvent(final String event, final JSONObject payload) {
        if (isInvalidAndroidVersion) {
            return;
        }
        Log.d(TAG, "sendEvent - event: " + event + " payload: " + payload);
        try {
            authManager.authorize(new NSRAuth() {
                public void authorized(boolean authorized) throws Exception {
                    if (!authorized) {
                        return;
                    }
                    JSONObject eventPayLoad = new JSONObject();
                    eventPayLoad.put("event", event);
                    eventPayLoad.put("timezone", TimeZone.getDefault().getID());
                    eventPayLoad.put("event_time", System.currentTimeMillis());
                    eventPayLoad.put("payload", payload);

                    JSONObject devicePayLoad = new JSONObject();
                    devicePayLoad.put("uid", DeviceUtils.getDeviceUid(context));
                    String pushToken = getPushToken();
                    if (pushToken != null) {
                        devicePayLoad.put("push_token", pushToken);
                    }
                    devicePayLoad.put("os", getOs());
                    devicePayLoad.put("version", Build.VERSION.RELEASE + " " + Build.VERSION_CODES.class.getFields()[android.os.Build.VERSION.SDK_INT].getName());
                    devicePayLoad.put("model", Build.MODEL);

                    JSONObject requestPayload = new JSONObject();
                    requestPayload.put("event", eventPayLoad);
                    requestPayload.put("user", getUser().toJsonObject(false));
                    requestPayload.put("device", devicePayLoad);

                    JSONObject headers = new JSONObject();
                    String token = getToken();
                    Log.d(TAG, "sendEvent token: " + token);
                    headers.put("ns_token", token);
                    headers.put("ns_lang", getLang());

                    Log.d(TAG, "requestPayload: " + requestPayload.toString());

                    getSecurityDelegate().secureRequest(context, "event", requestPayload, headers, new NSRSecurityResponse() {
                        public void completionHandler(JSONObject json, String error) throws Exception {
                            if (error == null) {
                                if (json.has("pushes")) {
                                    boolean skipPush = !json.has("skipPush") || json.getBoolean("skipPush");
                                    JSONArray pushes = json.getJSONArray("pushes");
                                    if (!skipPush) {
                                        if (pushes.length() > 0) {
                                            JSONObject push = pushes.getJSONObject(0);
                                            String imageUrl = push.has("imageUrl") ? push.getString("imageUrl") : null;
                                            String url = push.has("url") ? push.getString("url") : null;
                                            PendingIntent pendingIntent = (url != null && !"".equals(url)) ? PendingIntent.getActivity(context, (int) System.currentTimeMillis(), makeActivityWebView(url), PendingIntent.FLAG_UPDATE_CURRENT) : null;
                                            NSRNotification.sendNotification(context, push.getString("title"), push.getString("body"), imageUrl, pendingIntent);
                                        }
                                    } else {
                                        if (pushes.length() > 0) {
                                            JSONObject notification = pushes.getJSONObject(0);
                                            Log.d(TAG, notification.toString());
                                            showUrl(notification.getString("url"));
                                        }
                                    }
                                }
                            } else {
                                Log.e(TAG, "sendEvent secureRequest: " + error);
                            }
                        }
                    });
                }
            }, makeAuthArguments());
        } catch (Exception e) {
            Log.e(TAG, "sendEvent", e);
        }
    }

    public void showApp() {
        if (getAppURL() != null) {
            showUrl(getAppURL(), null);
        }
    }

    public void showApp(JSONObject params) {
        if (getAppURL() != null) {
            showUrl(getAppURL(), params);
        }
    }

    public void showUrl(String url) {
        showUrl(url, null);
    }

    public synchronized void showUrl(String url, JSONObject params) {
        if (isInvalidAndroidVersion) {
            return;
        }
        try {
            if (params != null && params.length() > 0) {
                Iterator<String> keys = params.keys();
                while (keys.hasNext()) {
                    String key = keys.next();
                    url += ((url.indexOf('?') < 0) ? "?" : "&") + key + "=" + URLEncoder.encode(params.getString(key), "UTF-8");
                }
            }
            if (activityWebView != null) {
                activityWebView.navigate(url);
            } else {
                context.startActivity(makeActivityWebView(url));
                activityWebView.navigate(url);
            }
        } catch (Exception e) {
            Log.e(TAG, "showUrl", e);
        }
    }

    protected Intent makeActivityWebView(String url) throws Exception {
        Intent intent = new Intent(context, NSRActivityWebView.class);
        intent.putExtra("url", url);
        return intent;
    }

    protected NSRUser getUser() {
        try {
            JSONObject user = getPersistenceManager().retrieveJson("user");
            return user != null ? new NSRUser(user) : null;
        } catch (Exception e) {
            Log.e(TAG, "getUser", e);
            return null;
        }
    }

    protected void setUser(NSRUser user) {
        getPersistenceManager().storeJson("user", user == null ? null : user.toJsonObject(true));
    }

    protected JSONObject getConf() {
        return getPersistenceManager().retrieveJson("conf");
    }

    @Override
    public void setConf(JSONObject conf) {
        getPersistenceManager().storeJson("conf", conf);
    }

    protected JSONObject getSettings() {
        return getPersistenceManager().retrieveJson("settings");
    }

    protected void setSettings(JSONObject settings) {
        getPersistenceManager().storeJson("settings", settings);
    }

    protected JSONObject getAuth() {
        return getPersistenceManager().retrieveJson("auth");
    }

    @Override
    public void setAuth(JSONObject auth) {
        getPersistenceManager().storeJson("auth", auth);
    }

    protected String getToken() {
        try {
            return getAuth().getString("token");
        } catch (Exception e) {
            Log.e(TAG, "getToken", e);
            return null;
        }
    }

    protected String getPushToken() {
        try {
            return getSettings().getString("push_token");
        } catch (Exception e) {
            Log.e(TAG, "getPushToken", e);
            return null;
        }
    }

    protected String getLang() {
        try {
            return getSettings().getString("ns_lang");
        } catch (Exception e) {
            Log.e(TAG, "getLang", e);
            return null;
        }
    }

    protected String getAppURL() {
        return getPersistenceManager().retrieveData("appURL");
    }

    @Override
    public void setAppURL(String appURL) {
        getPersistenceManager().storeData("appURL", appURL);
    }

    public PersistenceManager getPersistenceManager() {
        return persistenceManager;
    }

    public void loginExecuted(String url) {
        if (isInvalidAndroidVersion) {
            return;
        }
        try {
            JSONObject params = new JSONObject();
            params.put("loginExecuted", "yes");
            showUrl(url, params);
        } catch (Exception e) {
            Log.e(TAG, "loginExecuted", e);
        }
    }

    public void paymentExecuted(JSONObject paymentInfo, String url) {
        if (isInvalidAndroidVersion) {
            return;
        }
        try {
            JSONObject params = new JSONObject();
            params.put("paymentExecuted", paymentInfo.toString());
            showUrl(url, params);
        } catch (Exception e) {
            Log.e(TAG, "paymentExecuted", e);
        }
    }

    @Override
    public void onTraceDone(String traceType, JSONObject payload) {
        try {
            crunchEvent(traceType, payload);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void authorize(NSRAuth nsrAuth) {
        try {
            authManager.authorize(nsrAuth, makeAuthArguments());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
