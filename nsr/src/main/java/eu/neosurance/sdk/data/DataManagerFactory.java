package eu.neosurance.sdk.data;

import android.content.Context;

import eu.neosurance.sdk.platform.persistence.PersistenceFactory;
import eu.neosurance.sdk.platform.persistence.PersistenceManager;

public class DataManagerFactory {

    private final Context context;

    public DataManagerFactory(Context context) {
        this.context = context;
    }

    public DataManager makeDataManager() {
        PersistenceManager persistenceManager = new PersistenceFactory(context).makePersistenceManager();
        return new DataManager(persistenceManager);
    }
}
