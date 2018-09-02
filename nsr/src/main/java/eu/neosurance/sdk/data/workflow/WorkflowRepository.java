package eu.neosurance.sdk.data.workflow;

import android.util.Log;

import org.json.JSONObject;

import eu.neosurance.sdk.platform.persistence.PersistenceManager;


public class WorkflowRepository {
    private final PersistenceManager persistenceManager;

    public WorkflowRepository(PersistenceManager persistenceManager) {
        this.persistenceManager = persistenceManager;
    }

    public void setClass(String workflowDelegateClass) {
        persistenceManager.storeData("workflowDelegateClass", workflowDelegateClass);
    }

    public String getClassName() {
        return persistenceManager.retrieveData("workflowDelegateClass");
    }

}
