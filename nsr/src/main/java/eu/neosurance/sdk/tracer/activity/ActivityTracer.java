package eu.neosurance.sdk.tracer.activity;

import android.util.Log;

import org.json.JSONException;

import eu.neosurance.sdk.data.configuration.ConfigurationRepository;
import eu.neosurance.sdk.platform.activity.ActivityManager;
import eu.neosurance.sdk.tracer.Tracer;

public class ActivityTracer implements Tracer {

    private static final String TAG = ActivityTracer.class.getCanonicalName();

    private final ActivityManager activityManager;
    private final ConfigurationRepository configurationRepository;

    public ActivityTracer(ActivityManager activityManager, ConfigurationRepository configurationRepository) {
        this.activityManager = activityManager;
        this.configurationRepository = configurationRepository;
    }

    @Override
    public void trace() {
        Log.d(TAG, "traceActivity");
        try {
            if (configurationRepository.getConf() != null &&
                    configurationRepository.getConf().getJSONObject("activity").getInt("enabled") == 1) {
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
