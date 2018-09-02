package eu.neosurance.sdk.tracer.connection;

import android.util.Log;

import org.json.JSONObject;

import eu.neosurance.sdk.data.configuration.ConfigurationRepository;
import eu.neosurance.sdk.platform.connection.ConnectionManager;
import eu.neosurance.sdk.tracer.Tracer;
import eu.neosurance.sdk.tracer.TracerListener;

public class ConnectionTracer implements Tracer {

    private static final String TAG = ConnectionTracer.class.getCanonicalName();
    private static final String TRACE_TYPE = "connection";

    private final TracerListener tracerListener;
    private final ConfigurationRepository configurationRepository;
    private final ConnectionManager connectionManager;

    public ConnectionTracer(ConnectionManager connectionManager,
                            TracerListener tracerListener,
                            ConfigurationRepository configurationRepository) {
        this.connectionManager = connectionManager;
        this.tracerListener = tracerListener;
        this.configurationRepository = configurationRepository;
    }

    @Override
    public void trace() {
        Log.d(TAG, "traceConnection");
        try {
            if (configurationRepository.getConf().getJSONObject("connection").getInt("enabled") == 1) {
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
