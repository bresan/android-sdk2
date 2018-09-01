package eu.neosurance.sdk.tracer.location;

import android.content.Context;

import eu.neosurance.sdk.platform.location.AndroidLocationManager;
import eu.neosurance.sdk.platform.location.LocationManager;

public class LocationTracerFactory {

    private final Context context;

    public LocationTracerFactory(Context context) {
        this.context = context;
    }

    public LocationTracer makeLocationTracer() {
        LocationManager locationManager = makeLocationManager();
        LocationTracer locationTracer = new LocationTracer(locationManager);

        return locationTracer;
    }

    private LocationManager makeLocationManager() {
        LocationManager locationManager = new AndroidLocationManager(context);
        return locationManager;
    }
}
