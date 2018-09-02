package eu.neosurance.sdk.interactors.event;

import org.json.JSONObject;

import eu.neosurance.sdk.processors.event.EventProcessor;

public class SendEvent {
    private EventProcessor eventProcessor;

    public SendEvent(EventProcessor eventProcessor) {
        this.eventProcessor = eventProcessor;
    }

    public void execute(String event, JSONObject payload) {
        eventProcessor.sendEvent(event, payload);
    }
}
