package eu.neosurance.sdk.interactors.app;

import org.json.JSONObject;

import eu.neosurance.sdk.data.app.AppUrlRepository;
import eu.neosurance.sdk.webview.ActivityWebViewManager;

public class ShowApp {

    private AppUrlRepository appUrlRepository;
    private ActivityWebViewManager activityWebViewManager;

    public ShowApp(AppUrlRepository appUrlRepository,
                   ActivityWebViewManager activityWebViewManager) {
        this.appUrlRepository = appUrlRepository;
        this.activityWebViewManager = activityWebViewManager;
    }

    public void execute() {
        if (isValidUrl(appUrlRepository.getAppURL())) {
            activityWebViewManager.showUrl(appUrlRepository.getAppURL(), null);
        }
    }

    public void execute(JSONObject params) {
        if (isValidUrl(appUrlRepository.getAppURL())) {
            activityWebViewManager.showUrl(appUrlRepository.getAppURL(), params);
        }
    }

    public boolean isValidUrl(String url) {
        return url != null;
    }
}
