package eu.neosurance.sdk.platform.power;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

/**
 * This class is responsible for managing the battery status on Android devices
 */
public class AndroidPowerManager implements PowerManager {

    private final Context context;

    public AndroidPowerManager(Context context) {
        this.context = context;
    }

    @Override
    public int getBatteryLevel() {
        return getBatteryIntent().getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
    }

    @Override
    public String getStatus() {
        return getBatteryIntent().getIntExtra(BatteryManager.EXTRA_PLUGGED, -1) > 0 ?
                PowerStatus.PLUGGED.toString() : PowerStatus.UNPLUGGED.toString();
    }

    public Intent getBatteryIntent() {
        Intent batteryIntent = context.registerReceiver(null,
                new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        return batteryIntent;
    }
}
