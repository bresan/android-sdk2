package eu.neosurance.sdk.interactors.user;

import android.util.Log;

import eu.neosurance.sdk.job.JobManager;
import eu.neosurance.sdk.processors.auth.AuthProcessor;
import eu.neosurance.sdk.data.DataManager;
import eu.neosurance.sdk.interactors.DefaultUseCase;

public class ForgetUser implements DefaultUseCase<Void> {

    private static final String TAG = RegisterUser.class.getCanonicalName();

    private final DataManager dataManager;
    private JobManager jobManager;

    public ForgetUser(DataManager dataManager, JobManager jobManager) {
        this.dataManager = dataManager;
        this.jobManager = jobManager;
    }

    @Override
    public void execute(Void object) {
        Log.d(TAG, "forgetUser");
        dataManager.getConfigurationRepository().setConf(null);
        dataManager.getAuthRepository().setAuth(null);
        dataManager.getAppUrlRepository().setAppURL(null);
        dataManager.getUserRepository().setUser(null);

        jobManager.initJob();
    }
}
