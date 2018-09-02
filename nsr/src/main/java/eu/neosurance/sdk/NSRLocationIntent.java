package eu.neosurance.sdk;

import android.app.IntentService;
import android.content.Intent;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.location.LocationResult;

import org.json.JSONException;
import org.json.JSONObject;

public class NSRLocationIntent extends IntentService {

    public NSRLocationIntent() {
        super("NSRLocationIntent");
    }

    protected void onHandleIntent(Intent intent) {
        NSR nsr = NSR.getInstance(getApplicationContext());
        if (LocationResult.hasResult(intent)) {
            nsr.getTracerManager().getLocationTracer().stopTrace();
            try {
                LocationResult lr = LocationResult.extractResult(intent);
                Location lastLocation = lr.getLastLocation();
                Log.d(NSR.TAG, "NSRLocationIntent: " + lastLocation);
                if (nsr.getTracerManager().getLocationTracer().getLastLocation() != null) {
                    Log.d(NSR.TAG, "NSRLocationIntent distanceTo: " + lastLocation.distanceTo(nsr.getTracerManager().getLocationTracer().getLastLocation()));
                }
                if (nsr.getTracerManager().getLocationTracer().getLastLocation() == null ||
                        nsr.getTracerManager().getLocationTracer().getStillLocation() ||
                        lastLocationHigher(nsr, lastLocation)) {
                    JSONObject payload = new JSONObject();
                    payload.put("latitude", lastLocation.getLatitude());
                    payload.put("longitude", lastLocation.getLongitude());
                    payload.put("altitude", lastLocation.getAltitude());
                    nsr.getProcessorManager().getEventProcessor().crunchEvent("position", payload);
                    nsr.getTracerManager().getLocationTracer().setLastLocation(lastLocation);
                    nsr.getTracerManager().getLocationTracer().setStillLocation(false);
                }
            } catch (Exception e) {
                Log.e(NSR.TAG, "NSRLocationIntent", e);
            }
        } else {
            Log.d(NSR.TAG, "NSRLocationIntent: no result");
        }
    }

    private boolean lastLocationHigher(NSR nsr, Location lastLocation) throws JSONException {
        return lastLocation.distanceTo(nsr.getTracerManager().getLocationTracer().getLastLocation()) > nsr.getDataManager().getConfigurationRepository().getConf().getJSONObject("position").getDouble("meters");
    }
}
