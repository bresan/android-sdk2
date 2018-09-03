package eu.neosurance.sdk.interactors;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import eu.neosurance.sdk.data.DataManager;
import eu.neosurance.sdk.data.app.AppUrlRepository;
import eu.neosurance.sdk.data.auth.AuthRepository;
import eu.neosurance.sdk.data.configuration.ConfigurationRepository;
import eu.neosurance.sdk.data.settings.SettingsRepository;
import eu.neosurance.sdk.data.user.UserRepository;
import eu.neosurance.sdk.interactors.user.ForgetUser;
import eu.neosurance.sdk.job.JobManager;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class ForgetUserTest {

    ForgetUser forgetUser;
    JobManager jobManager;
    DataManager dataManager;

    ConfigurationRepository configurationRepository;
    AuthRepository authRepository;
    AppUrlRepository appUrlRepository;
    UserRepository userRepository;

    @Before
    public void setup() {
        jobManager = mock(JobManager.class);

        dataManager = mock(DataManager.class);

        configurationRepository = mock(ConfigurationRepository.class);
        authRepository = mock(AuthRepository.class);
        appUrlRepository = mock(AppUrlRepository.class);
        userRepository = mock(UserRepository.class);

        stubRepositoriesReturn();

        forgetUser = new ForgetUser(dataManager, jobManager);
    }

    private void stubRepositoriesReturn() {
        when(dataManager.getConfigurationRepository())
                .thenReturn(configurationRepository);

        when(dataManager.getAppUrlRepository())
                .thenReturn(appUrlRepository);

        when(dataManager.getAuthRepository())
                .thenReturn(authRepository);

        when(dataManager.getUserRepository())
                .thenReturn(userRepository);
    }

    @Test
    public void shouldInitJobOnExecute() {
        forgetUser.execute(null);

        verify(jobManager).initJob();
    }

    @Test
    public void shouldClearRepositoriesOnExecute() {
        stubRepositoriesReturn();
        forgetUser.execute(null);

        verify(configurationRepository).setConf(null);
        verify(appUrlRepository).setAppURL(null);
        verify(authRepository).setAuth(null);
        verify(userRepository).setUser(null);
    }
}