package eu.neosurance.sdk.utils;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.Iterator;

import eu.neosurance.sdk.NSRActivityWebView;


public class ActivityWebViewManager {

    private static final String TAG = ActivityWebViewManager.class.getCanonicalName();

    private final Context context;
    private NSRActivityWebView activityWebView;

    public ActivityWebViewManager(Context context) {
        this.context = context;
    }

    public void registerWebView(NSRActivityWebView activityWebView) {
        if (this.getActivityWebView() != null)
            this.getActivityWebView().finish();

        this.setActivityWebView(activityWebView);
    }

    public void clearWebView() {
        this.activityWebView = null;
    }

    public void setActivityWebView(NSRActivityWebView activityWebView) {
        this.activityWebView = activityWebView;
    }

    public NSRActivityWebView getActivityWebView() {
        return activityWebView;
    }

    public static Intent makeActivityWebView(Context context, String url) {
        Intent intent = new Intent(context, NSRActivityWebView.class);
        intent.putExtra("url", url);
        return intent;
    }

    public synchronized void showUrl(String url, JSONObject params) {
        PrecheckUtils.guaranteeMinimalAndroidVersion();

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
                context.startActivity(makeActivityWebView(context, url));
                activityWebView.navigate(url);
            }
        } catch (Exception e) {
            Log.e(TAG, "showUrl", e);
        }
    }

    public void showUrl(String url) {
        showUrl(url, null);
    }
}
