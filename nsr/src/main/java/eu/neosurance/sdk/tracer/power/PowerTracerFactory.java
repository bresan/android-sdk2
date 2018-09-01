package eu.neosurance.sdk.tracer.power;

import android.content.Context;

import eu.neosurance.sdk.platform.power.AndroidPowerManager;
import eu.neosurance.sdk.platform.power.PowerManager;
import eu.neosurance.sdk.tracer.TracerListener;

public class PowerTracerFactory {

    private final Context context;
    private final TracerListener tracerListener;

    public PowerTracerFactory(Context context, TracerListener tracerListener) {
        this.context = context;
        this.tracerListener = tracerListener;
    }

    public PowerTracer makePowerTracer() {
        PowerManager powerManager = makePowerManager();
        return new PowerTracer(tracerListener, powerManager);
    }

    private PowerManager makePowerManager() {
        return new AndroidPowerManager(context);
    }
}
