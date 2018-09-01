package eu.neosurance.sdk.platform.activity;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.ActivityRecognitionClient;

import eu.neosurance.sdk.NSRActivityIntent;

public class AndroidActivityManager implements ActivityManager {

    private static final String TAG = AndroidActivityManager.class.getCanonicalName();

    private final Context context;

    private ActivityRecognitionClient activity = null;
    private String lastActivity = null;
    private PendingIntent activityIntent = null;

    public AndroidActivityManager(Context context) {
        this.context = context;
    }

    @Override
    public void initActivity() {
        if (activity == null) {
            Log.d(TAG, "initActivity");
            Intent intent = new Intent(context, NSRActivityIntent.class);
            activity = ActivityRecognition.getClient(context);
            activityIntent = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }
    }

    @Override
    public void requestUpdates(long time) {
        activity.requestActivityUpdates(time, activityIntent);
    }

    @Override
    public void stopTraceActivity() {
        if (activity != null) {
            Log.d(TAG, "stopTraceActivity");
            activity.removeActivityUpdates(activityIntent);
        }
    }

    @Override
    public String getLastActivity() {
        return lastActivity;
    }

    @Override
    public void setLastActivity(String lastActivity) {
        this.lastActivity = lastActivity;
    }
}
