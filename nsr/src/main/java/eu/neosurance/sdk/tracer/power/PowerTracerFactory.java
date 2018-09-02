package eu.neosurance.sdk.tracer.power;

import android.content.Context;

import eu.neosurance.sdk.data.configuration.ConfigurationRepository;
import eu.neosurance.sdk.platform.power.AndroidPowerManager;
import eu.neosurance.sdk.platform.power.PowerManager;
import eu.neosurance.sdk.tracer.TracerListener;

public class PowerTracerFactory {

    private final Context context;
    private final TracerListener tracerListener;
    private final ConfigurationRepository configurationRepository;


    public PowerTracerFactory(Context context, TracerListener tracerListener, ConfigurationRepository configurationRepository) {
        this.context = context;
        this.tracerListener = tracerListener;
        this.configurationRepository = configurationRepository;
    }

    public PowerTracer makePowerTracer() {
        PowerManager powerManager = makePowerManager();
        return new PowerTracer(tracerListener, powerManager, configurationRepository);
    }

    private PowerManager makePowerManager() {
        return new AndroidPowerManager(context);
    }
}
