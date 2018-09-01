package eu.neosurance.sdk.tracer;

import android.content.Context;

import eu.neosurance.sdk.platform.connection.ConnectionManager;
import eu.neosurance.sdk.tracer.activity.ActivityTracer;
import eu.neosurance.sdk.tracer.activity.ActivityTracerFactory;
import eu.neosurance.sdk.tracer.connection.ConnectionTracer;
import eu.neosurance.sdk.tracer.connection.ConnectionTracerFactory;
import eu.neosurance.sdk.tracer.location.LocationTracer;
import eu.neosurance.sdk.tracer.location.LocationTracerFactory;
import eu.neosurance.sdk.tracer.power.PowerTracer;
import eu.neosurance.sdk.tracer.power.PowerTracerFactory;

public class TracerFactory {

    public final Context context;
    private final TracerListener tracerListener;

    public TracerFactory(Context context,
                         TracerListener tracerListener) {
        this.context = context;
        this.tracerListener = tracerListener;
    }

    public LocationTracer makeLocationTracer() {
        return new LocationTracerFactory(context).makeLocationTracer();
    }

    public PowerTracer makePowerTracer() {
        return new PowerTracerFactory(context, tracerListener).makePowerTracer();
    }

    public ActivityTracer makeActivityTracer() {
        return new ActivityTracerFactory(context).makeActivityTracer();
    }

    public ConnectionTracer makeConnectionTracer() {
        return new ConnectionTracerFactory(context, tracerListener).makeConnectionTracer();
    }
}
