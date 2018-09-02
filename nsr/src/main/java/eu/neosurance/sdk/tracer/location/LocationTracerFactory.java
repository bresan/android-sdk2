package eu.neosurance.sdk.tracer.location;

import android.content.Context;

import eu.neosurance.sdk.data.configuration.ConfigurationRepository;
import eu.neosurance.sdk.platform.location.AndroidLocationManager;
import eu.neosurance.sdk.platform.location.LocationManager;

public class LocationTracerFactory {

    private final Context context;
    private final ConfigurationRepository configurationRepository;


    public LocationTracerFactory(Context context, ConfigurationRepository configurationRepository) {
        this.context = context;
        this.configurationRepository = configurationRepository;
    }

    public LocationTracer makeLocationTracer() {
        LocationManager locationManager = makeLocationManager();
        LocationTracer locationTracer = new LocationTracer(locationManager, configurationRepository);

        return locationTracer;
    }

    private LocationManager makeLocationManager() {
        LocationManager locationManager = new AndroidLocationManager(context);
        return locationManager;
    }
}
