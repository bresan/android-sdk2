package eu.neosurance.sdk.tracer.connection;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import org.json.JSONObject;

import eu.neosurance.sdk.NSR;
import eu.neosurance.sdk.platform.connection.ConnectionManager;
import eu.neosurance.sdk.tracer.Tracer;
import eu.neosurance.sdk.tracer.TracerListener;

public class ConnectionTracer implements Tracer {

    private static final String TAG = ConnectionTracer.class.getCanonicalName();
    private static final String TRACE_TYPE = "connection";

    private final TracerListener tracerListener;
    private ConnectionManager connectionManager;

    public ConnectionTracer(ConnectionManager connectionManager, TracerListener tracerListener) {
        this.connectionManager = connectionManager;
        this.tracerListener = tracerListener;
    }

    @Override
    public void trace(JSONObject conf) {
        Log.d(TAG, "traceConnection");
        try {
            if (conf.getJSONObject("connection").getInt("enabled") == 1) {
                String connection = connectionManager.getConnectionType();

                if (connection != null && !connection.equals(connectionManager.getLastConnection())) {

                    JSONObject payload = new JSONObject();
                    payload.put("type", connection);

                    tracerListener.onTraceDone(TRACE_TYPE, payload);
                    connectionManager.setLastConnection(connection);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "traceConnection", e);
        }
    }

    @Override
    public void stopTrace() {

    }
}
