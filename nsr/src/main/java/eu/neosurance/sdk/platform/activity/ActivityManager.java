package eu.neosurance.sdk.platform.activity;

public interface ActivityManager {
    void initActivity();
    void requestUpdates(long time);
    void stopTraceActivity();
    String getLastActivity();

    void setLastActivity(String lastActivity);
}
