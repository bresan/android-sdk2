package eu.neosurance.sdk;

import org.json.JSONObject;

import eu.neosurance.sdk.data.DataManager;
import eu.neosurance.sdk.platform.persistence.PersistenceManager;

public class DataManagerFactory {
    public static DataManager makeDataManager() {
        return new DataManager(new PersistenceManager() {
            @Override
            public void storeJson(String key, JSONObject value) {

            }

            @Override
            public void storeData(String key, String value) {

            }

            @Override
            public JSONObject retrieveJson(String key) {
                return null;
            }

            @Override
            public String retrieveData(String key) {
                return null;
            }
        });
    }
}
