package eu.neosurance.sdk.platform.persistence;

import android.content.Context;

public class PersistenceFactory {

    public Context context;

    public PersistenceFactory(Context context) {
        this.context = context;
    }

    public PersistenceManager makePersistenceManager() {
        return new SharedPreferencesManager(this.context);
    }
}
