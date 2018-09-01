package eu.neosurance.sdk.tracer.connection;


import android.content.Context;

import eu.neosurance.sdk.platform.connection.AndroidConnectionManager;
import eu.neosurance.sdk.platform.connection.ConnectionManager;
import eu.neosurance.sdk.tracer.TracerListener;

public class ConnectionTracerFactory {

    private final Context context;
    private final TracerListener tracerListener;

    public ConnectionTracerFactory(Context context, TracerListener tracerListener) {
        this.context = context;
        this.tracerListener = tracerListener;
    }

    public ConnectionTracer makeConnectionTracer() {
        ConnectionManager connectionManager = makeConnectionManager();
        ConnectionTracer connectionTracer = new ConnectionTracer(connectionManager, tracerListener);

        return connectionTracer;
    }

    private ConnectionManager makeConnectionManager() {
        return new AndroidConnectionManager(context);
    }
}
