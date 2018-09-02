package eu.neosurance.sdk.data.auth;

import android.util.Log;

import org.json.JSONObject;

import eu.neosurance.sdk.data.settings.SettingsRepository;
import eu.neosurance.sdk.platform.persistence.PersistenceManager;

public class AuthRepository {

    private static final String TAG = SettingsRepository.class.getCanonicalName();

    private PersistenceManager persistenceManager;

    public AuthRepository(PersistenceManager persistenceManager) {
        this.persistenceManager = persistenceManager;
    }

    public JSONObject getAuth() {
        return persistenceManager.retrieveJson("auth");
    }

    public void setAuth(JSONObject auth) {
        persistenceManager.storeJson("auth", auth);
    }

    public String getToken() {
        try {
            return getAuth().getString("token");
        } catch (Exception e) {
            Log.e(TAG, "getToken", e);
            return null;
        }
    }

}
