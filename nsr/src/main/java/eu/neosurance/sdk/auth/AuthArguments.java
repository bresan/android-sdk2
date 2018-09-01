package eu.neosurance.sdk.auth;

import org.json.JSONObject;

import eu.neosurance.sdk.NSRUser;

public class AuthArguments {
    private JSONObject auth;
    private NSRUser user;
    private JSONObject settings;
    private String version;
    private String os;
    private JSONObject conf;

    private AuthArguments(Builder builder) {
        setAuth(builder.auth);
        setUser(builder.user);
        setSettings(builder.settings);
        setVersion(builder.version);
        setOs(builder.os);
        setConf(builder.conf);
    }

    public JSONObject getAuth() {
        return auth;
    }

    public void setAuth(JSONObject auth) {
        this.auth = auth;
    }

    public NSRUser getUser() {
        return user;
    }

    public void setUser(NSRUser user) {
        this.user = user;
    }

    public JSONObject getSettings() {
        return settings;
    }

    public void setSettings(JSONObject settings) {
        this.settings = settings;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public JSONObject getConf() {
        return conf;
    }

    public void setConf(JSONObject conf) {
        this.conf = conf;
    }


    public static final class Builder {
        private JSONObject auth;
        private NSRUser user;
        private JSONObject settings;
        private String version;
        private String os;
        private JSONObject conf;

        public Builder() {
        }

        public Builder auth(JSONObject val) {
            auth = val;
            return this;
        }

        public Builder user(NSRUser val) {
            user = val;
            return this;
        }

        public Builder settings(JSONObject val) {
            settings = val;
            return this;
        }

        public Builder version(String val) {
            version = val;
            return this;
        }

        public Builder os(String val) {
            os = val;
            return this;
        }

        public Builder conf(JSONObject val) {
            conf = val;
            return this;
        }

        public AuthArguments build() {
            if (auth == null || user == null || settings == null || version == null || os == null ||
                    conf == null) {
                throw new IllegalArgumentException("Missing fields");
            }

            return new AuthArguments(this);
        }
    }
}
