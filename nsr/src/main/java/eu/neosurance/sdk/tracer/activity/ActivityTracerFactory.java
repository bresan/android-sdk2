package eu.neosurance.sdk.tracer.activity;

import android.content.Context;

import eu.neosurance.sdk.platform.activity.ActivityManager;
import eu.neosurance.sdk.platform.activity.AndroidActivityManager;

public class ActivityTracerFactory {

    private final Context context;

    public ActivityTracerFactory(Context context) {
        this.context = context;
    }

    public ActivityTracer makeActivityTracer() {
        ActivityManager activityManager = makeActivityManager();
        ActivityTracer activityTracer = new ActivityTracer(activityManager);

        return activityTracer;
    }

    public ActivityManager makeActivityManager() {
        return new AndroidActivityManager(context);
    }
}
