package eu.neosurance.sdk;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import eu.neosurance.sdk.data.DataManager;
import eu.neosurance.sdk.data.auth.AuthRepository;
import eu.neosurance.sdk.data.settings.SettingsRepository;
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
import eu.neosurance.sdk.utils.SetupUtils;

import static eu.neosurance.sdk.utils.PrecheckUtils.guaranteeMinimalAndroidVersion;

public class NSR implements AuthListener {

    protected static final String TAG = "nsr";

    private static NSR instance = null;
    private Context context;

    private DependencyProvider dependencyProvider;

    /*
      Accessible methods over public API
     */

    /**
     * This method is used to get the instance of Neosurance client
     *
     * @param context the context to be used
     * @return the {@link NSR} class instance
     */
    public static NSR getInstance(Context context) {
        if (instance == null) {
            instance = new NSR(context);
            instance = InstanceManager.setupNewInstance(instance);
        } else {
            instance.context = context;
        }
        return instance;
    }

    /**
     * This method is used to setup the NSR client receiving. It expects a JSONObject.
     *
     * @param settings the JSON object with the settings to be used
     */
    public void setup(final JSONObject settings) {
        SetupUtils.performSetup(context, getDataManager(), settings,
                Constants.PERMISSIONS_MULTIPLE_ACCESSLOCATION);
    }

    /**
     * This method is used to perform the login of a given user
     *
     * @param user the user to perform the login
     */
    public void registerUser(NSRUser user) {
        guaranteeMinimalAndroidVersion();
        getForgetUserUseCase().execute(null);
        getRegisterUserUseCase().execute(user);
    }

    /**
     * This method is used to forget the login of the current user
     */
    public void forgetUser() {
        guaranteeMinimalAndroidVersion();
        getForgetUserUseCase().execute(null);
    }

    /**
     * This method is used to submit a given event with a payload
     *
     * @param event   the event to be submitted
     * @param payload the payload message to be submited with the event
     */
    public void sendEvent(final String event, final JSONObject payload) {
        getSendEventUseCase().execute(event, payload);
    }

    /**
     * This method is used to indicate when the user has done login
     *
     * @param url the url to be displayed to the user
     */
    public void loginExecuted(String url) {
        getPerformLoginUseCase().execute(url);
    }

    /**
     * This method is used to indicate when the user has done payment
     *
     * @param paymentInfo the payment info
     * @param url         the url to be displayed to the user
     */
    public void paymentExecuted(JSONObject paymentInfo, String url) {
        getPaymentDoneUseCase().execute(paymentInfo, url);
    }

    /**
     * This method is called to display the insurance screen. For sending with extra parameters,
     * please check {@link #showApp(JSONObject)}
     */
    public void showApp() {
        getShowAppUseCase().execute();
    }

    /**
     * This method is used to display the insurance screen with extra parameters
     *
     * @param params the params to be passed
     */
    public void showApp(JSONObject params) {
        getShowAppUseCase().execute(params);
    }

    /**
     * This method is used to set the workflow delegate
     *
     * @param workflowDelegate workflow to be used
     */
    public void setWorkflowDelegate(NSRWorkflowDelegate workflowDelegate) {
        guaranteeMinimalAndroidVersion();
        getDataManager().getWorkflowRepository().setClass(workflowDelegate.getClass().getName());
        dependencyProvider.setWorkflowDelegate(workflowDelegate);
    }

    /*
      The section below contains methods that are not available for access over the public API.
     */

    /**
     * Default constructor
     *
     * @param context the activity context
     */
    private NSR(Context context) {
        this.context = context;
        initDependencyProvider();
    }

    /**
     * This method is used to initialize our dependency provider
     */
    private void initDependencyProvider() {
        this.dependencyProvider = new DependencyProvider(context, this);
    }


    /*
      Security related methods
     */

    protected NSRSecurityDelegate getSecurityDelegate() {
        return dependencyProvider.getSecurityDelegate();
    }

    protected void setSecurityDelegate(NSRSecurityDelegate securityDelegate) {
        guaranteeMinimalAndroidVersion();

        getDataManager().getSecurityRepository().setClass(securityDelegate.getClass().getName());
        dependencyProvider.setSecurityDelegate(securityDelegate);

        getProcessorManager().getAuthProcessor().setSecurityDelegate(securityDelegate);
        getProcessorManager().getRequestProcessor().setSecurityDelegate(securityDelegate);
    }


    /**
     * Workflow delegate methods
     */
    protected NSRWorkflowDelegate getWorkflowDelegate() {
        return dependencyProvider.getWorkflowDelegate();
    }


    /*
      Repository related methods
     */

    protected ProcessorManager getProcessorManager() {
        return dependencyProvider.getProcessorManager();
    }

    protected SettingsRepository getSettingsRepository() {
        return getDataManager().getSettingsRepository();
    }

    protected AuthRepository getAuthRepository() {
        return getDataManager().getAuthRepository();
    }


    /*
      Manager objects
     */

    protected TracerManager getTracerManager() {
        return dependencyProvider.getTracerManager();
    }

    protected ActivityWebViewManager getActivityWebViewManager() {
        return dependencyProvider.getActivityWebViewManager();
    }

    protected JobManager getJobManager() {
        return dependencyProvider.getJobManager();
    }

    protected DataManager getDataManager() {
        return dependencyProvider.getDataManager();
    }

    /*
      Use cases
     */

    /**
     * Accessor for retrieving the use case for registering an user
     *
     * @return the {@link RegisterUser} use case
     */
    protected RegisterUser getRegisterUserUseCase() {
        return dependencyProvider.getRegisterUserUseCase();
    }

    /**
     * Accessor for retrieving the use case for forgetting an user
     *
     * @return the {@link ForgetUser} use case
     */
    protected ForgetUser getForgetUserUseCase() {
        return dependencyProvider.getForgetUserUseCase();
    }

    /**
     * Accessor for retrieving the use case for showing the insurance app
     *
     * @return the {@link ShowApp} use case
     */
    protected ShowApp getShowAppUseCase() {
        return dependencyProvider.getShowAppUseCase();
    }

    /**
     * Accessor for retrieving the use case for sending an event
     *
     * @return the {@link SendEvent} use case
     */
    protected SendEvent getSendEventUseCase() {
        return dependencyProvider.getSendEventUseCase();
    }

    /**
     * Accessor for retrieving the use case for showing the insurance app
     *
     * @return the {@link ShowApp} use case
     */
    protected PaymentDone getPaymentDoneUseCase() {
        return dependencyProvider.getPaymentDoneUseCase();
    }

    /**
     * Accessor for retrieving the use case for performing the login
     *
     * @return the {@link PerformLogin} use case
     */
    protected PerformLogin getPerformLoginUseCase() {
        return dependencyProvider.getPerformLoginUseCase();
    }


    /**
     * Auth listener related methods
     *
     * @param conf    current configuration
     * @param oldConf old configuration
     * @return if it needs to init the job
     * @throws JSONException in case there's an issue parsing the json
     */
    @Override
    public boolean needsInitJob(JSONObject conf, JSONObject oldConf) throws JSONException {
        return getJobManager().needsInitJob(conf, oldConf);
    }

    /**
     * This method is used to init our job
     */
    @Override
    public void initJob() {
        getJobManager().initJob();
    }
}
