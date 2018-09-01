package eu.neosurance.sdk.tracer.location;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import eu.neosurance.sdk.platform.location.LocationManager;
import eu.neosurance.sdk.tracer.Tracer;

public class LocationTracer implements Tracer {

    private static final String TAG = LocationTracer.class.getCanonicalName();
    private LocationManager locationManager;

    private boolean stillLocation;

    public LocationTracer(LocationManager locationManager) {
        this.locationManager = locationManager;
    }

    public LocationManager getLocationManager() {
        return locationManager;
    }

    @Override
    public void trace(JSONObject conf) {
        Log.d(TAG, "traceLocation");
        try {
            if (locationManager.hasLocationPermission()) {
                if (conf != null && conf.getJSONObject("position").getInt("enabled") == 1) {
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
