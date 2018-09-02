package eu.neosurance.sdk.data.security;

import eu.neosurance.sdk.platform.persistence.PersistenceManager;

public class SecurityRepository {
    private final PersistenceManager persistenceManager;

    public SecurityRepository(PersistenceManager persistenceManager) {
        this.persistenceManager = persistenceManager;
    }

    public void setClass(String securityDelegateClass) {
        persistenceManager.storeData("securityDelegateClass", securityDelegateClass);
    }

    public String getClassName() {
        return persistenceManager.retrieveData("securityDelegateClass");
    }

}
