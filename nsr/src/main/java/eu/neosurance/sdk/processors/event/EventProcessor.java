package eu.neosurance.sdk.processors.event;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.TimeZone;

import eu.neosurance.sdk.Constants;
import eu.neosurance.sdk.NSRAuth;
import eu.neosurance.sdk.eventwebview.NSREventWebView;
import eu.neosurance.sdk.NSRNotification;
import eu.neosurance.sdk.data.DataManager;
import eu.neosurance.sdk.processors.auth.AuthProcessor;
import eu.neosurance.sdk.webview.ActivityWebViewManager;
import eu.neosurance.sdk.utils.DeviceUtils;
import eu.neosurance.sdk.processors.request.RequestProcessor;
import eu.neosurance.sdk.utils.PrecheckUtils;

public class EventProcessor {

    private static final String TAG = EventProcessor.class.getCanonicalName();
    private final Context context;

    private final AuthProcessor authProcessor;
    private final RequestProcessor requestProcessor;
    private final ActivityWebViewManager activityWebViewManager;
    private final NSREventWebView eventWebView;
    private final DataManager dataManager;

    public EventProcessor(Context context,
                          AuthProcessor authProcessor,
                          RequestProcessor requestProcessor,
                          ActivityWebViewManager activityWebViewManager,
                          NSREventWebView eventWebView,
                          DataManager dataManager) {
        this.context = context;
        this.authProcessor = authProcessor;
        this.requestProcessor = requestProcessor;
        this.activityWebViewManager = activityWebViewManager;
        this.eventWebView = eventWebView;
        this.dataManager = dataManager;
    }

    public void sendEvent(final String event, final JSONObject payload) {
        PrecheckUtils.guaranteeMinimalAndroidVersion();

        final EventArguments arguments = makeEventArguments();
        Log.d(TAG, "sendEvent - event: " + event + " payload: " + payload);
        try {
            authProcessor.authorize(new NSRAuth() {
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
                    String pushToken = arguments.getPushToken();
                    if (pushToken != null) {
                        devicePayLoad.put("push_token", pushToken);
                    }
                    devicePayLoad.put("os", arguments.getOs());
                    devicePayLoad.put("version", Build.VERSION.RELEASE + " " + Build.VERSION_CODES.class.getFields()[Build.VERSION.SDK_INT].getName());
                    devicePayLoad.put("model", Build.MODEL);

                    JSONObject requestPayload = new JSONObject();
                    requestPayload.put("event", eventPayLoad);
                    requestPayload.put("user", arguments.getUser().toJsonObject(false));
                    requestPayload.put("device", devicePayLoad);

                    JSONObject headers = new JSONObject();
                    String token = arguments.getToken();
                    Log.d(TAG, "sendEvent token: " + token);
                    headers.put("ns_token", token);
                    headers.put("ns_lang", arguments.getLang());

                    Log.d(TAG, "requestPayload: " + requestPayload.toString());

                    requestProcessor.performSecureRequest("event", requestPayload, headers, new RequestProcessor.RequestListener() {
                        @Override
                        public void onRequestSuccess(JSONObject json) throws Exception {
                            handleSuccessResponse(json);
                        }

                        @Override
                        public void onRequestFailure(String error) {

                        }
                    });
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "sendEvent", e);
        }
    }

    private void handleSuccessResponse(JSONObject json) throws JSONException {
        if (json.has("pushes")) {
            boolean skipPush = !json.has("skipPush") || json.getBoolean("skipPush");
            JSONArray pushes = json.getJSONArray("pushes");
            if (!skipPush) {
                handleNotSkipPush(pushes);
            } else {
                if (pushes.length() > 0) {
                    JSONObject notification = pushes.getJSONObject(0);
                    Log.d(TAG, notification.toString());
                    activityWebViewManager.showUrl(notification.getString("url"), null);
                }
            }
        }
    }

    private void handleNotSkipPush(JSONArray pushes) throws JSONException {
        if (pushes.length() > 0) {
            JSONObject push = pushes.getJSONObject(0);
            String imageUrl = push.has("imageUrl") ? push.getString("imageUrl") : null;
            String url = push.has("url") ? push.getString("url") : null;

            Intent intentWebView = ActivityWebViewManager.makeActivityWebView(context, url);

            PendingIntent pendingIntent;
            if (url != null && !"".equals(url)) {
                pendingIntent = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), intentWebView, PendingIntent.FLAG_UPDATE_CURRENT);
            } else {
                pendingIntent = null;
            }

            NSRNotification.sendNotification(context, push.getString("title"), push.getString("body"), imageUrl, pendingIntent);
        }
    }

    public void crunchEvent(final String event, final JSONObject payload) throws Exception {
        JSONObject conf = dataManager.getConfigurationRepository().getConf();
        if (conf != null && conf.has("local_tracking") && conf.getBoolean("local_tracking")) {
            Log.d(TAG, "crunchEvent: " + event + " payload: " + payload.toString());
            if (eventWebView != null) {
                eventWebView.crunchEvent(event, payload);
            }
        } else {
            sendEvent(event, payload);
        }
    }

    public EventArguments makeEventArguments() {
        EventArguments eventArguments = new EventArguments();
        eventArguments.setLang(dataManager.getSettingsRepository().getLang());
        eventArguments.setPushToken(dataManager.getSettingsRepository().getPushToken());
        eventArguments.setToken(dataManager.getAuthRepository().getToken());
        eventArguments.setUser(dataManager.getUserRepository().getUser());
        eventArguments.setOs(Constants.getOs());

        return eventArguments;
    }

}