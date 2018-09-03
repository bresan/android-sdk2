package eu.neosurance.sdk.interactors;

import junit.framework.TestCase;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import eu.neosurance.sdk.data.app.AppUrlRepository;
import eu.neosurance.sdk.interactors.app.ShowApp;
import eu.neosurance.sdk.webview.ActivityWebViewManager;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class ShowAppTest extends TestCase {

    AppUrlRepository appUrlRepository;

    ActivityWebViewManager activityWebViewManager;

    ShowApp showApp;


    @Before
    public void setup() {
        appUrlRepository = mock(AppUrlRepository.class);
        activityWebViewManager = mock(ActivityWebViewManager.class);
        showApp = new ShowApp(appUrlRepository, activityWebViewManager);
    }

    @Test
    public void isUrlValidForNullValue() {
        boolean result = showApp.isValidUrl(null);

        assertEquals(result, false);
    }

    @Test
    public void isUrlValidForValidValue() {
        boolean result = showApp.isValidUrl("http://url.com");

        assertEquals(result, true);
    }

    @Test
    public void isShowUrlCalledOnExecuteWithoutParams() {
        stubAppUrlRepository();
        showApp.execute();

        verify(activityWebViewManager, times(1)).showUrl("", null);
    }

    @Test
    public void isShowUrlCalledOnExecuteWithParams() {
        stubAppUrlRepository();

        JSONObject jsonObject = new JSONObject();
        showApp.execute(jsonObject);

        verify(activityWebViewManager, times(1)).showUrl("", jsonObject);
    }

    void stubAppUrlRepository() {
        when(appUrlRepository.getAppURL()).thenReturn("");
    }
}