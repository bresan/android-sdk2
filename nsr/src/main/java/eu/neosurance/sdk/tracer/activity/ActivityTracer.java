package eu.neosurance.sdk.tracer.activity;

import android.app.Activity;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import eu.neosurance.sdk.platform.activity.ActivityManager;
import eu.neosurance.sdk.tracer.Tracer;

public class ActivityTracer implements Tracer {

    private static final String TAG = ActivityTracer.class.getCanonicalName();

    private final ActivityManager activityManager;

    public ActivityTracer(ActivityManager activityManager) {
        this.activityManager = activityManager;
    }

    @Override
    public void trace(JSONObject conf) {
        Log.d(TAG, "traceActivity");
        try {
            if (conf != null && conf.getJSONObject("activity").getInt("enabled") == 1) {
                activityManager.initActivity();
                Log.d(TAG, "requestActivityUpdates");
                activityManager.stopTraceActivity();
                activityManager.requestUpdates(1000);
            }
        } catch (JSONException e) {
            Log.e(TAG, "traceActivity", e);
        }
    }

    @Override
    public void stopTrace() {
        activityManager.stopTraceActivity();
    }

    public String getLastActivity() {
        return activityManager.getLastActivity();
    }

    public void setLastActivity(String lastActivity) {
        activityManager.setLastActivity(lastActivity);
    }
}
