package eu.neosurance.sdk.job;

import android.content.Context;
import android.util.Log;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;

import org.json.JSONException;
import org.json.JSONObject;

import eu.neosurance.sdk.eventwebview.NSREventWebView;
import eu.neosurance.sdk.NSRJobService;
import eu.neosurance.sdk.data.DataManager;
import eu.neosurance.sdk.processors.ProcessorManager;
import eu.neosurance.sdk.tracer.TracerManager;
import eu.neosurance.sdk.utils.ActivityWebViewManager;
import eu.neosurance.sdk.utils.EventWebViewManager;

public class JobManager {

    private static final String TAG = JobManager.class.getCanonicalName();
    private static final String JOB_TAG = "nsrJob";

    private final Context context;
    private final TracerManager tracerManager;
    private final DataManager dataManager;
    private final EventWebViewManager eventWebViewManager;
    private ProcessorManager processorManager;
    private ActivityWebViewManager activityWebViewManager;

    public JobManager(Context context, TracerManager tracerManager,
                      DataManager dataManager,
                      EventWebViewManager eventWebViewManager,
                      ProcessorManager processorManager,
                      ActivityWebViewManager activityWebViewManager) {
        this.context = context;
        this.tracerManager = tracerManager;
        this.dataManager = dataManager;
        this.eventWebViewManager = eventWebViewManager;
        this.processorManager = processorManager;
        this.activityWebViewManager = activityWebViewManager;
    }

    public void initJob() {
        Log.d(TAG, "initJob");
        try {
            tracerManager.getActivityTracer().stopTrace();
            tracerManager.getLocationTracer().stopTrace();
            new FirebaseJobDispatcher(new GooglePlayDriver(context)).cancel(JOB_TAG);
            JSONObject conf = dataManager.getConfigurationRepository().getConf();
            if (conf != null) {
                if (eventWebViewManager.getEventWebView() == null &&
                        conf.has("local_tracking") &&
                        conf.getBoolean("local_tracking")) {
                    Log.d(TAG, "Making NSREventWebView");

                    NSREventWebView nsrEventWebView = new NSREventWebView(context, processorManager, dataManager, activityWebViewManager);

                    eventWebViewManager.setEventWebView(nsrEventWebView);
                    eventWebViewManager.setEventWebViewSynchTime(System.currentTimeMillis() / 1000);
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

    public boolean needsInitJob(JSONObject conf, JSONObject oldConf) throws JSONException {
        return (oldConf == null) ||
                (conf.getInt("time") != oldConf.getInt("time")) ||
                (eventWebViewManager.getEventWebView() == null && conf.has("local_tracking") &&
                        conf.getBoolean("local_tracking"));
    }
}
