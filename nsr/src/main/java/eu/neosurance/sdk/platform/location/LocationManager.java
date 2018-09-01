package eu.neosurance.sdk.platform.location;

import android.location.Location;

import org.json.JSONObject;

public interface LocationManager {
    Location getLastLocation();

    void setLastLocation(Location lastLocation);

    boolean hasLocationPermission();

    void initLocation();

    void stopTraceLocation();

    void requestLocationUpdates();
}
