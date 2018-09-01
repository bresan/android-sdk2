package eu.neosurance.sdk.platform.connection;

public enum ConnectionType {
    WIFI,
    MOBILE,
    UNKNOWN;

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
