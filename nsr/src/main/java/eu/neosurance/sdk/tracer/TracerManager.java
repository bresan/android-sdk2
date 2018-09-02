package eu.neosurance.sdk.tracer;

import android.content.Context;

import org.json.JSONObject;

import eu.neosurance.sdk.configuration.ConfigurationManager;
import eu.neosurance.sdk.tracer.activity.ActivityTracer;
import eu.neosurance.sdk.tracer.connection.ConnectionTracer;
import eu.neosurance.sdk.tracer.location.LocationTracer;
import eu.neosurance.sdk.tracer.power.PowerTracer;

public class TracerManager {
//
//    private final Context context;
//    private final TracerListener tracerListener;
//    private final ConfigurationManager configurationManager;

    private TracerFactory tracerFactory;
    private PowerTracer powerTracer;
    private LocationTracer locationTracer;
    private ActivityTracer activityTracer;
    private ConnectionTracer connectionTracer;

    private JSONObject conf;
//
//    public TracerManager(Context context, TracerListener listener, ConfigurationManager configurationManager) {
//        this.context = context;
//        this.tracerListener = listener;
//        this.configurationManager = configurationManager;
//
//        initTracers();
//    }
//
//    private void initTracers() {
//        tracerFactory = new TracerFactory(context, tracerListener);
//        powerTracer = tracerFactory.makePowerTracer();
//        locationTracer = tracerFactory.makeLocationTracer();
//        activityTracer = tracerFactory.makeActivityTracer();
//        connectionTracer = tracerFactory.makeConnectionTracer();
//    }
//
//    public PowerTracer getPowerTracer() {
//        updateConfForTracers();
//        return powerTracer;
//    }
//
//    public LocationTracer getLocationTracer() {
//        updateConfForTracers();
//        return locationTracer;
//    }
//
//    public ActivityTracer getActivityTracer() {
//        updateConfForTracers();
//        return activityTracer;
//    }
//
//    public ConnectionTracer getConnectionTracer() {
//        updateConfForTracers();
//        return connectionTracer;
//    }
//
//    private void updateConfForTracers() {
//        this.conf = configurationManager.getConf();
//        powerTracer.setConf(conf);
//        locationTracer.setConf(conf);
//        activityTracer.setConf(conf);
//        connectionTracer.setConf(conf);
//    }
}
