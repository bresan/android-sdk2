package eu.neosurance.sdk.data;

import eu.neosurance.sdk.data.app.AppUrlRepository;
import eu.neosurance.sdk.data.auth.AuthRepository;
import eu.neosurance.sdk.data.configuration.ConfigurationRepository;
import eu.neosurance.sdk.data.workflow.WorkflowRepository;
import eu.neosurance.sdk.data.security.SecurityRepository;
import eu.neosurance.sdk.data.settings.SettingsRepository;
import eu.neosurance.sdk.data.user.UserRepository;
import eu.neosurance.sdk.platform.persistence.PersistenceManager;

public class DataManager {

    private PersistenceManager persistenceManager;

    private AuthRepository authRepository;
    private ConfigurationRepository configurationRepository;
    private SettingsRepository settingsRepository;
    private UserRepository userRepository;
    private AppUrlRepository appUrlRepository;
    private WorkflowRepository workflowRepository;
    private SecurityRepository securityRepository;


    public DataManager(PersistenceManager persistenceManager) {
        this.persistenceManager = persistenceManager;
        initRepositories();
    }

    private void initRepositories() {
        authRepository = new AuthRepository(persistenceManager);
        configurationRepository = new ConfigurationRepository(persistenceManager);
        settingsRepository = new SettingsRepository(persistenceManager);
        userRepository = new UserRepository(persistenceManager);
        appUrlRepository = new AppUrlRepository(persistenceManager);
        workflowRepository = new WorkflowRepository(persistenceManager);
        securityRepository = new SecurityRepository(persistenceManager);
    }

    public WorkflowRepository getWorkflowRepository() {
        return workflowRepository;
    }

    public SecurityRepository getSecurityRepository() {
        return securityRepository;
    }

    public AppUrlRepository getAppUrlRepository() {
        return appUrlRepository;
    }

    public PersistenceManager getPersistenceManager() {
        return persistenceManager;
    }

    public AuthRepository getAuthRepository() {
        return authRepository;
    }

    public ConfigurationRepository getConfigurationRepository() {
        return configurationRepository;
    }

    public SettingsRepository getSettingsRepository() {
        return settingsRepository;
    }

    public UserRepository getUserRepository() {
        return userRepository;
    }
}
