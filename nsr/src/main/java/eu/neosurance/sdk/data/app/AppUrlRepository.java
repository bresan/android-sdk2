package eu.neosurance.sdk.data.app;

import android.util.Log;

import org.json.JSONObject;

import eu.neosurance.sdk.data.auth.AuthRepository;
import eu.neosurance.sdk.platform.persistence.PersistenceManager;

public class AppUrlRepository {

    private static final String TAG = AppUrlRepository.class.getCanonicalName();

    private PersistenceManager persistenceManager;

    public AppUrlRepository(PersistenceManager persistenceManager) {
        this.persistenceManager = persistenceManager;
    }

    public String getAppURL() {
        return persistenceManager.retrieveData("appURL");
    }

    public void setAppURL(String appURL) {
        persistenceManager.storeData("appURL", appURL);
    }

}
