package eu.neosurance.sdk.configuration;

import org.json.JSONObject;

import eu.neosurance.sdk.platform.persistence.PersistenceManager;

public class ConfigurationManager {

    private final PersistenceManager persistenceManager;

    public ConfigurationManager(PersistenceManager persistenceManager) {
        this.persistenceManager = persistenceManager;
    }

    public JSONObject getConf() {
        return persistenceManager.retrieveJson("conf");
    }

    public void setConf(JSONObject conf) {
        persistenceManager.storeJson("conf", conf);
    }
}
