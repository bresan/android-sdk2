package eu.neosurance.sdk.data.configuration;

import org.json.JSONObject;

import eu.neosurance.sdk.platform.persistence.PersistenceManager;

public class ConfigurationRepository {

    private final PersistenceManager persistenceManager;

    public ConfigurationRepository(PersistenceManager persistenceManager) {
        this.persistenceManager = persistenceManager;
    }

    public JSONObject getConf() {
        return persistenceManager.retrieveJson("conf");
    }

    public void setConf(JSONObject conf) {
        persistenceManager.storeJson("conf", conf);
    }
}
