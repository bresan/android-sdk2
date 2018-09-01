package eu.neosurance.sdk.auth;

import org.json.JSONObject;

public interface AuthListener {
    void setAuth(JSONObject auth);

    void setConf(JSONObject conf);

    void setAppURL(String appUrl);

    boolean needsInitJob(JSONObject conf, JSONObject oldConf) throws Exception;

    void initJob();

    void synchEventWebView();
}
