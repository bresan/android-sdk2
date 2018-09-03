package eu.neosurance.sdk;

import android.content.Context;

import java.util.Objects;

import eu.neosurance.sdk.data.DataManager;
import eu.neosurance.sdk.data.DataManagerFactory;
import eu.neosurance.sdk.interactors.app.ShowApp;
import eu.neosurance.sdk.interactors.event.SendEvent;
import eu.neosurance.sdk.interactors.login.PerformLogin;
import eu.neosurance.sdk.interactors.payment.PaymentDone;
import eu.neosurance.sdk.interactors.user.ForgetUser;
import eu.neosurance.sdk.interactors.user.RegisterUser;
import eu.neosurance.sdk.job.JobManager;
import eu.neosurance.sdk.processors.ProcessorManager;
import eu.neosurance.sdk.processors.auth.AuthListener;
import eu.neosurance.sdk.tracer.TracerManager;
import eu.neosurance.sdk.webview.ActivityWebViewManager;
import eu.neosurance.sdk.webview.EventWebViewManager;

public class DependencyProvider {

    private final Context context;
    private AuthListener authListener;

    // Persistence
    private DataManager dataManager;

    // WebView related managers
    private ActivityWebViewManager activityWebViewManager;
    private EventWebViewManager eventWebViewManager;

    // Processor related (action, event, auth, request)
    private ProcessorManager processorManager;

    // Job related
    private JobManager jobManager;

    // Tracer related (activity, location, connection, power)
    private TracerManager tracerManager;

    // Use cases
    private RegisterUser registerUserUseCase;
    private ForgetUser forgetUserUseCase;
    private ShowApp showAppUseCase;
    private PaymentDone paymentDoneUseCase;
    private SendEvent sendEventUseCase;
    private PerformLogin performLoginUseCase;

    // Workflow and Security delegates
    private NSRWorkflowDelegate workflowDelegate;
    private NSRSecurityDelegate securityDelegate;

    public DependencyProvider(Context context, AuthListener authListener) {
        this.context = context;
        this.authListener = authListener;

        initDependencies();
    }

    /**
     * Pay attention to the order of the instantiations!
     * <p>
     * TODO: check for null, if it is, initialize new object
     */
    private void initDependencies() {
        dataManager = provideDataManager();

        activityWebViewManager = provideActivityWebViewManager();
        eventWebViewManager = provideEventWebViewManager();

        processorManager = provideProcessorManager(authListener);

        tracerManager = provideTracerManager();
        jobManager = provideJobManager();

        registerUserUseCase = provideRegisterUserUseCase();
        forgetUserUseCase = provideForgetUserUseCase();
        showAppUseCase = provideShowAppUseCase();
        paymentDoneUseCase = providePaymentDoneUseCase();
        sendEventUseCase = provideSendEventUseCase();
        performLoginUseCase = providePerformLoginUsecase();
    }

    private void checkForNullDependencies(Object... dependencies) {
        for (Object dependency : dependencies) {
            Objects.requireNonNull(dependency);
        }
    }

    private PaymentDone providePaymentDoneUseCase() {
        checkForNullDependencies(activityWebViewManager);
        return new PaymentDone(activityWebViewManager);
    }

    private SendEvent provideSendEventUseCase() {
        checkForNullDependencies(processorManager.getEventProcessor());
        return new SendEvent(processorManager.getEventProcessor());
    }

    private PerformLogin providePerformLoginUsecase() {
        checkForNullDependencies(activityWebViewManager);
        return new PerformLogin(activityWebViewManager);
    }

    private ShowApp provideShowAppUseCase() {
        checkForNullDependencies(dataManager.getAppUrlRepository(), activityWebViewManager);
        return new ShowApp(dataManager.getAppUrlRepository(), activityWebViewManager);
    }

    private RegisterUser provideRegisterUserUseCase() {
        checkForNullDependencies(dataManager.getUserRepository(), processorManager.getAuthProcessor());
        return new RegisterUser(dataManager.getUserRepository(), processorManager.getAuthProcessor());
    }

    private ForgetUser provideForgetUserUseCase() {
        checkForNullDependencies(dataManager, jobManager);
        return new ForgetUser(dataManager, jobManager);
    }

    private TracerManager provideTracerManager() {
        checkForNullDependencies(context, dataManager.getConfigurationRepository(), processorManager);
        return new TracerManager(context, dataManager.getConfigurationRepository(), processorManager);
    }

    private DataManager provideDataManager() {
        checkForNullDependencies(context);
        return new DataManagerFactory(context).makeDataManager();
    }

    private ActivityWebViewManager provideActivityWebViewManager() {
        checkForNullDependencies(context);
        return new ActivityWebViewManager(context);
    }

    private EventWebViewManager provideEventWebViewManager() {
        return new EventWebViewManager();
    }

    private ProcessorManager provideProcessorManager(AuthListener authListener) {
        checkForNullDependencies(context, dataManager, eventWebViewManager, activityWebViewManager);
        return new ProcessorManager(context, dataManager, eventWebViewManager, activityWebViewManager, authListener);
    }

    private JobManager provideJobManager() {
        checkForNullDependencies(context, tracerManager, dataManager, eventWebViewManager, processorManager, activityWebViewManager);
        return new JobManager(context, tracerManager, dataManager, eventWebViewManager, processorManager, activityWebViewManager);
    }

    public PaymentDone getPaymentDoneUseCase() {
        return paymentDoneUseCase;
    }

    public SendEvent getSendEventUseCase() {
        return sendEventUseCase;
    }

    public PerformLogin getPerformLoginUseCase() {
        return performLoginUseCase;
    }

    public ShowApp getShowAppUseCase() {
        return showAppUseCase;
    }

    public RegisterUser getRegisterUserUseCase() {
        return registerUserUseCase;
    }

    public ForgetUser getForgetUserUseCase() {
        return forgetUserUseCase;
    }

    public DataManager getDataManager() {
        return dataManager;
    }

    public ActivityWebViewManager getActivityWebViewManager() {
        return activityWebViewManager;
    }

    public EventWebViewManager getEventWebViewManager() {
        return eventWebViewManager;
    }

    public ProcessorManager getProcessorManager() {
        return processorManager;
    }

    public JobManager getJobManager() {
        return jobManager;
    }

    public TracerManager getTracerManager() {
        return tracerManager;
    }

    public void setWorkflowDelegate(NSRWorkflowDelegate workflowDelegate) {
        this.workflowDelegate = workflowDelegate;
    }

    public void setSecurityDelegate(NSRSecurityDelegate securityDelegate) {
        this.securityDelegate = securityDelegate;
    }

    public NSRWorkflowDelegate getWorkflowDelegate() {
        return workflowDelegate;
    }

    public NSRSecurityDelegate getSecurityDelegate() {
        return securityDelegate;
    }
}
