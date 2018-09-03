package eu.neosurance.sdk.job;

import android.content.Context;
import android.test.suitebuilder.annotation.MediumTest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import eu.neosurance.sdk.data.DataManager;
import eu.neosurance.sdk.processors.ProcessorManager;
import eu.neosurance.sdk.tracer.TracerManager;
import eu.neosurance.sdk.tracer.activity.ActivityTracer;
import eu.neosurance.sdk.tracer.location.LocationTracer;
import eu.neosurance.sdk.webview.ActivityWebViewManager;
import eu.neosurance.sdk.webview.EventWebViewManager;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class JobManagerTest {

    JobManager jobManager;
    Context context;

    @Mock
    TracerManager tracerManager;

    @Mock
    ActivityTracer activityTracer;

    @Mock
    LocationTracer locationTracer;

    @Mock
    DataManager dataManager;
    @Mock
    EventWebViewManager eventWebViewManager;
    @Mock
    ProcessorManager processorManager;
    @Mock
    ActivityWebViewManager activityWebViewManager;

    @Before
    public void setUp() throws Exception {
        context = mock(Context.class);

        stubTracerManager();
        jobManager = new JobManager(context, tracerManager, dataManager, eventWebViewManager,
                processorManager, activityWebViewManager);
    }

    private void stubTracerManager() {
        when(tracerManager.getActivityTracer())
                .thenReturn(activityTracer);

        when(tracerManager.getLocationTracer())
                .thenReturn(locationTracer);
    }

    @Test
    public void shouldStopActivityTracerOnInitJob() {
        jobManager.initJob();

        verify(activityTracer).stopTrace();
    }

    @Test
    public void shouldStopLocationTracerOnInitJob() {
        jobManager.initJob();

        verify(locationTracer).stopTrace();
    }
}