package eu.neosurance.sdk.tracer.activity;

import android.content.Context;

import eu.neosurance.sdk.data.configuration.ConfigurationRepository;
import eu.neosurance.sdk.platform.activity.ActivityManager;
import eu.neosurance.sdk.platform.activity.AndroidActivityManager;

public class ActivityTracerFactory {

    private final Context context;
    private final ConfigurationRepository configurationRepository;


    public ActivityTracerFactory(Context context, ConfigurationRepository configurationRepository) {
        this.context = context;
        this.configurationRepository = configurationRepository;
    }

    public ActivityTracer makeActivityTracer() {
        ActivityManager activityManager = makeActivityManager();
        ActivityTracer activityTracer = new ActivityTracer(activityManager, configurationRepository);

        return activityTracer;
    }

    public ActivityManager makeActivityManager() {
        return new AndroidActivityManager(context);
    }
}
