package eu.neosurance.sdk.tracer.location;

import android.location.Location;
import android.util.Log;

import org.json.JSONException;

import eu.neosurance.sdk.data.configuration.ConfigurationRepository;
import eu.neosurance.sdk.platform.location.LocationManager;
import eu.neosurance.sdk.tracer.Tracer;

public class LocationTracer implements Tracer {

    private static final String TAG = LocationTracer.class.getCanonicalName();
    private final LocationManager locationManager;
    private final ConfigurationRepository configurationRepository;

    private boolean stillLocation;

    public LocationTracer(LocationManager locationManager,
                          ConfigurationRepository configurationRepository) {
        this.locationManager = locationManager;
        this.configurationRepository = configurationRepository;
    }

    public LocationManager getLocationManager() {
        return locationManager;
    }

    @Override
    public void trace() {
        Log.d(TAG, "traceLocation");
        try {
            if (locationManager.hasLocationPermission()) {
                if (configurationRepository.getConf() != null &&
                        configurationRepository.getConf().getJSONObject("position").getInt("enabled") == 1) {
                    locationManager.initLocation();
                    Log.d(TAG, "requestLocationUpdates");
                    locationManager.stopTraceLocation();
                    locationManager.requestLocationUpdates();
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, "traceLocation", e);
        }
    }

    @Override
    public void stopTrace() {
        this.locationManager.stopTraceLocation();
    }

    public void setLastLocation(Location lastLocation) {
        this.getLocationManager().setLastLocation(lastLocation);
    }

    public Location getLastLocation() {
        return this.getLocationManager().getLastLocation();
    }

    public boolean getStillLocation() {
        return stillLocation;
    }

    public void setStillLocation(boolean stillLocation) {
        this.stillLocation = stillLocation;
    }
}
