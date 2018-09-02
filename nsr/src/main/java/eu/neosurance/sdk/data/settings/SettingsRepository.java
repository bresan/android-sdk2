package eu.neosurance.sdk.data.settings;

import android.util.Log;

import org.json.JSONObject;

import eu.neosurance.sdk.platform.persistence.PersistenceManager;

public class SettingsRepository {

    private static final String TAG = SettingsRepository.class.getCanonicalName();

    private final PersistenceManager persistenceManager;

    public SettingsRepository(PersistenceManager persistenceManager) {
        this.persistenceManager = persistenceManager;
    }

    public JSONObject getSettings() {
        return persistenceManager.retrieveJson("settings");
    }

    public void setSettings(JSONObject settings) {
        persistenceManager.storeJson("settings", settings);
    }

    public String getPushToken() {
        try {
            return getSettings().getString("push_token");
        } catch (Exception e) {
            Log.e(TAG, "getPushToken", e);
            return null;
        }
    }

    public String getLang() {
        try {
            return getSettings().getString("ns_lang");
        } catch (Exception e) {
            Log.e(TAG, "getLang", e);
            return null;
        }
    }
}
