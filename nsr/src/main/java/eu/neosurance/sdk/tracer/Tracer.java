package eu.neosurance.sdk.tracer;

import org.json.JSONObject;

public interface Tracer {
    void trace(JSONObject conf);
    void stopTrace();
}
