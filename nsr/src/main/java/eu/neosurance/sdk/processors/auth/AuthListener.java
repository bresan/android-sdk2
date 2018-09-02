package eu.neosurance.sdk.processors.auth;

import org.json.JSONException;
import org.json.JSONObject;

public interface AuthListener {

    boolean needsInitJob(JSONObject conf, JSONObject oldConf) throws JSONException;

    void initJob();
}
