package eu.neosurance.sdk.interactors;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import eu.neosurance.sdk.interactors.event.SendEvent;
import eu.neosurance.sdk.processors.event.EventProcessor;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(JUnit4.class)
public class SendEventTest {

    private EventProcessor eventProcessor;
    private SendEvent sendEvent;

    @Before
    public void setup() {
        eventProcessor = mock(EventProcessor.class);
        sendEvent = new SendEvent(eventProcessor);
    }

    @Test
    public void shouldCallEventProcessorOnExecute() {
        sendEvent.execute("event", null);
        verify(eventProcessor, times(1)).sendEvent("event", null);
    }
}
