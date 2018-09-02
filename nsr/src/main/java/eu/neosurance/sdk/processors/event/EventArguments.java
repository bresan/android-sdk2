package eu.neosurance.sdk.processors.event;

import eu.neosurance.sdk.NSRUser;

public class EventArguments {
    private String pushToken;
    private String os;
    private NSRUser user;
    private String token;
    private String lang;

    public EventArguments() {

    }

    public String getPushToken() {
        return pushToken;
    }

    public void setPushToken(String pushToken) {
        this.pushToken = pushToken;
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public NSRUser getUser() {
        return user;
    }

    public void setUser(NSRUser user) {
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }
}
