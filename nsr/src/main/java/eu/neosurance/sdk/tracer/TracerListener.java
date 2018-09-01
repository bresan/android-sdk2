package eu.neosurance.sdk.tracer;

import org.json.JSONObject;

public interface TracerListener {
    void onTraceDone(String traceType, JSONObject payload);
}
