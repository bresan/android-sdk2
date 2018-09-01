package eu.neosurance.sdk.platform.connection;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class AndroidConnectionManager implements ConnectionManager {

    private final Context context;
    private String lastConnection;

    public AndroidConnectionManager(Context context) {
        this.context = context;
    }

    @SuppressLint("MissingPermission")
    @Override
    public String getConnectionType() {
        NetworkInfo info = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            switch (info.getType()) {
                case ConnectivityManager.TYPE_WIFI:
                    return ConnectionType.WIFI.toString();

                case ConnectivityManager.TYPE_MOBILE:
                    return ConnectionType.MOBILE.toString();
            }
        }

        return ConnectionType.UNKNOWN.toString();
    }

    @Override
    public String getLastConnection() {
        return lastConnection;
    }

    @Override
    public void setLastConnection(String connection) {
        this.lastConnection = connection;
    }
}
