package eu.neosurance.sdk.platform.persistence;

import org.json.JSONObject;

public interface PersistenceManager {
    void storeJson(String key, JSONObject value);
    void storeData(String key, String value);
    JSONObject retrieveJson(String key);
    String retrieveData(String key);
}
