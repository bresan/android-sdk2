package eu.neosurance.sdk.interactors.app;

import org.json.JSONObject;

import eu.neosurance.sdk.data.app.AppUrlRepository;
import eu.neosurance.sdk.utils.ActivityWebViewManager;

public class ShowApp {

    private AppUrlRepository appUrlRepository;
    private ActivityWebViewManager activityWebViewManager;

    public ShowApp(AppUrlRepository appUrlRepository,
                   ActivityWebViewManager activityWebViewManager) {
        this.appUrlRepository = appUrlRepository;
        this.activityWebViewManager = activityWebViewManager;
    }

    public void execute() {
        if (appUrlRepository.getAppURL() != null) {
            activityWebViewManager.showUrl(appUrlRepository.getAppURL(), null);
        }
    }

    public void execute(JSONObject params) {
        if (appUrlRepository.getAppURL() != null) {
            activityWebViewManager.showUrl(appUrlRepository.getAppURL(), params);
        }
    }
}
