package eu.neosurance.sdk.tracer;

import android.content.Context;

import org.json.JSONObject;

import eu.neosurance.sdk.data.configuration.ConfigurationRepository;
import eu.neosurance.sdk.processors.ProcessorManager;
import eu.neosurance.sdk.tracer.activity.ActivityTracer;
import eu.neosurance.sdk.tracer.connection.ConnectionTracer;
import eu.neosurance.sdk.tracer.location.LocationTracer;
import eu.neosurance.sdk.tracer.power.PowerTracer;

public class TracerManager implements TracerListener {

    private final Context context;
    private final ConfigurationRepository configurationRepository;
    private ProcessorManager processorManager;

    private TracerFactory tracerFactory;
    private PowerTracer powerTracer;
    private LocationTracer locationTracer;
    private ActivityTracer activityTracer;
    private ConnectionTracer connectionTracer;

    public TracerManager(Context context,
                         ConfigurationRepository configurationRepository,
                         ProcessorManager processorManager) {
        this.context = context;
        this.configurationRepository = configurationRepository;
        this.processorManager = processorManager;

        setupTracers();
    }

    private void setupTracers() {
        tracerFactory = new TracerFactory(context, this, configurationRepository);
        powerTracer = tracerFactory.makePowerTracer();
        locationTracer = tracerFactory.makeLocationTracer();
        activityTracer = tracerFactory.makeActivityTracer();
        connectionTracer = tracerFactory.makeConnectionTracer();
    }

    public void initAllTracers() {
        getLocationTracer().trace();
        getActivityTracer().trace();
        getPowerTracer().trace();
        getConnectionTracer().trace();
    }

    public PowerTracer getPowerTracer() {
        return powerTracer;
    }

    public LocationTracer getLocationTracer() {
        return locationTracer;
    }

    public ActivityTracer getActivityTracer() {
        return activityTracer;
    }

    public ConnectionTracer getConnectionTracer() {
        return connectionTracer;
    }

    @Override
    public void onTraceDone(String traceType, JSONObject payload) {
        try {
            processorManager.getEventProcessor().crunchEvent(traceType, payload);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
