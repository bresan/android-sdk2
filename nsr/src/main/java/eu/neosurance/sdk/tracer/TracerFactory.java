package eu.neosurance.sdk.tracer;

import android.content.Context;

import eu.neosurance.sdk.data.configuration.ConfigurationRepository;
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
    private final ConfigurationRepository configurationRepository;


    public TracerFactory(Context context,
                         TracerListener tracerListener,
                         ConfigurationRepository configurationRepository) {
        this.context = context;
        this.tracerListener = tracerListener;
        this.configurationRepository = configurationRepository;
    }

    public LocationTracer makeLocationTracer() {
        return new LocationTracerFactory(context, configurationRepository).makeLocationTracer();
    }

    public PowerTracer makePowerTracer() {
        return new PowerTracerFactory(context, tracerListener, configurationRepository).makePowerTracer();
    }

    public ActivityTracer makeActivityTracer() {
        return new ActivityTracerFactory(context, configurationRepository).makeActivityTracer();
    }

    public ConnectionTracer makeConnectionTracer() {
        return new ConnectionTracerFactory(context, tracerListener, configurationRepository).makeConnectionTracer();
    }
}
