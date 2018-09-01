package eu.neosurance.sdk.platform.connection;

public interface ConnectionManager {
    String getConnectionType();
    String getLastConnection();
    void setLastConnection(String connection);
}
