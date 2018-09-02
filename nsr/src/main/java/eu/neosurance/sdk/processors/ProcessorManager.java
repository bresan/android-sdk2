package eu.neosurance.sdk.processors;

import android.content.Context;

import eu.neosurance.sdk.data.DataManager;
import eu.neosurance.sdk.job.JobManager;
import eu.neosurance.sdk.processors.action.ActionProcessor;
import eu.neosurance.sdk.processors.auth.AuthListener;
import eu.neosurance.sdk.processors.auth.AuthProcessor;
import eu.neosurance.sdk.processors.event.EventProcessor;
import eu.neosurance.sdk.processors.request.RequestProcessor;
import eu.neosurance.sdk.utils.ActivityWebViewManager;
import eu.neosurance.sdk.utils.EventWebViewManager;

public class ProcessorManager {

    private final Context context;
    private final DataManager dataManager;
    private final EventWebViewManager eventWebViewManager;
    private final ActivityWebViewManager activityWebViewManager;
    private AuthListener authListener;

    private RequestProcessor requestProcessor;
    private AuthProcessor authProcessor;
    private EventProcessor eventProcessor;
    private ActionProcessor actionProcessor;

    public ProcessorManager(Context context, DataManager dataManager,
                            EventWebViewManager eventWebViewManager,
                            ActivityWebViewManager activityWebViewManager,
                            AuthListener authListener) {
        this.context = context;
        this.dataManager = dataManager;
        this.eventWebViewManager = eventWebViewManager;
        this.activityWebViewManager = activityWebViewManager;
        this.authListener = authListener;

        initProcessors();
    }

    private void initProcessors() {
        // Move to Processor Factory
        authProcessor = new AuthProcessor(context, dataManager, eventWebViewManager, authListener);
        requestProcessor = new RequestProcessor(context);

        eventProcessor = new EventProcessor(context, authProcessor, requestProcessor,
                activityWebViewManager, eventWebViewManager.getEventWebView(), dataManager);

        actionProcessor = new ActionProcessor(authProcessor, requestProcessor, dataManager);
    }

    public RequestProcessor getRequestProcessor() {
        return requestProcessor;
    }

    public AuthProcessor getAuthProcessor() {
        return authProcessor;
    }

    public EventProcessor getEventProcessor() {
        return eventProcessor;
    }

    public ActionProcessor getActionProcessor() {
        return actionProcessor;
    }
}
