package eu.neosurance.sdk.data.user;

import android.util.Log;

import org.json.JSONObject;

import eu.neosurance.sdk.NSRUser;
import eu.neosurance.sdk.platform.persistence.PersistenceManager;

public class UserRepository {

    private static final String TAG = UserRepository.class.getCanonicalName();

    private PersistenceManager persistenceManager;

    public UserRepository(PersistenceManager persistenceManager) {
        this.persistenceManager = persistenceManager;
    }

    public NSRUser getUser() {
        try {
            JSONObject user = persistenceManager.retrieveJson("user");
            return user != null ? new NSRUser(user) : null;
        } catch (Exception e) {
            Log.e(TAG, "getUser", e);
            return null;
        }
    }

    public void setUser(NSRUser user) {
        persistenceManager.storeJson("user", user == null ? null : user.toJsonObject(true));
    }

}
