package eu.neosurance.sdk.platform.location;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import eu.neosurance.sdk.NSRLocationIntent;

public class AndroidLocationManager implements LocationManager {
    private static final String TAG = AndroidLocationManager.class.getCanonicalName();

    private Location lastLocation = null;

    private final Context context;
    private FusedLocationProviderClient locationClient;
    private LocationRequest locationRequest;
    private PendingIntent locationIntent = null;

    public AndroidLocationManager(Context context) {
        this.context = context;
    }

    @Override
    public Location getLastLocation() {
        return lastLocation;
    }

    @Override
    public void setLastLocation(Location lastLocation) {
        this.lastLocation = lastLocation;
    }

    @Override
    public boolean hasLocationPermission() {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
                && (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED);
    }

    @Override
    public void initLocation() {
        if (locationClient == null) {
            Intent intent = new Intent(context, NSRLocationIntent.class);
            int flag = PendingIntent.FLAG_UPDATE_CURRENT;

            Log.d(TAG, "initLocation");
            locationClient = LocationServices.getFusedLocationProviderClient(context);
            locationIntent = PendingIntent.getService(context, 0, intent, flag);
            locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(0);
            locationRequest.setFastestInterval(0);
            locationRequest.setNumUpdates(1);
        }
    }

    @Override
    public void stopTraceLocation() {
        if (locationClient != null) {
            Log.d(TAG, "stopTraceLocation");
            locationClient.removeLocationUpdates(locationIntent);
        }
    }

    @Override
    public void requestLocationUpdates() {
        locationClient.requestLocationUpdates(locationRequest, locationIntent);
    }
}
