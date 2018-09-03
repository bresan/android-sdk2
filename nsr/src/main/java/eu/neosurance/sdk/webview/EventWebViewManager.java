package eu.neosurance.sdk.webview;

import eu.neosurance.sdk.eventwebview.NSREventWebView;

public class EventWebViewManager {

    private NSREventWebView eventWebView = null;
    private long eventWebViewSynchTime = 0;

    public NSREventWebView getEventWebView() {
        return eventWebView;
    }

    public void setEventWebView(NSREventWebView eventWebView) {
        this.eventWebView = eventWebView;
    }

    public long getEventWebViewSynchTime() {
        return eventWebViewSynchTime;
    }

    public void setEventWebViewSynchTime(long eventWebViewSynchTime) {
        this.eventWebViewSynchTime = eventWebViewSynchTime;
    }

    public void synchEventWebView() {
        long t = System.currentTimeMillis() / 1000;
        if (getEventWebView() != null && t - getEventWebViewSynchTime() > (60 * 60 * 8)) {
            getEventWebView().synch();
            setEventWebViewSynchTime(t);
        }
    }
}
