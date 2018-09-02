package eu.neosurance.sdk.tracer.power;

import android.util.Log;

import org.json.JSONObject;

import eu.neosurance.sdk.data.configuration.ConfigurationRepository;
import eu.neosurance.sdk.platform.power.PowerManager;
import eu.neosurance.sdk.tracer.Tracer;
import eu.neosurance.sdk.tracer.TracerListener;

public class PowerTracer implements Tracer {
    private static final String TAG = PowerTracer.class.getCanonicalName();
    private static final String TRACE_TYPE = "power";

    private final TracerListener listener;
    private final PowerManager powerManager;
    private final ConfigurationRepository configurationRepository;

    private String lastPower = null;
    private int lastPowerLevel = 0;

    public PowerTracer(TracerListener listener, PowerManager powerManager,
                       ConfigurationRepository configurationRepository) {
        this.listener = listener;
        this.powerManager = powerManager;
        this.configurationRepository = configurationRepository;
    }

    @Override
    public void trace() {
        Log.d(TAG, "tracePower");
        try {
            if (configurationRepository.getConf().getJSONObject("power").getInt("enabled") == 1) {

                int powerLevel = powerManager.getBatteryLevel();
                String power = powerManager.getStatus();

                if (!power.equals(lastPower) || Math.abs(powerLevel - lastPowerLevel) >= 5) {
                    JSONObject payload = new JSONObject();
                    payload.put("type", power);
                    payload.put("level", powerLevel);

                    this.listener.onTraceDone(TRACE_TYPE, payload);
                    lastPower = power;
                    lastPowerLevel = powerLevel;
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "tracePower", e);
        }
    }

    @Override
    public void stopTrace() {
        // nothing to do here..
    }
}
