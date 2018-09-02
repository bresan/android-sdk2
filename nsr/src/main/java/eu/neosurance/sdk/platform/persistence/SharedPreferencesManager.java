package eu.neosurance.sdk.platform.persistence;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

public class SharedPreferencesManager implements PersistenceManager {

    private final Context context;
    private static final String PREFS_NAME = "NSRSDK";
    private static final String DEFAULT_EMPTY_JSON = "{}";
    private static final String DEFAULT_EMPTY_DATA = "";

    public SharedPreferencesManager(Context context) {
        this.context = context;
    }

    @Override
    public void storeJson(String key, JSONObject value) {
        this.storeData(key, value.toString());
    }

    @Override
    public void storeData(String key, String value) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        if (value != null) {
            editor.putString(key, value);
        } else {
            editor.remove(key);
        }
        editor.commit();
    }

    @Override
    public JSONObject retrieveJson(String key) {
        try {
            if (getSharedPreferences().contains(key))
                return new JSONObject(getSharedPreferences().getString(key, DEFAULT_EMPTY_JSON));
            else
                return null;
        } catch (JSONException e) {
            return null;
        }
    }

    @Override
    public String retrieveData(String key) {
        if (getSharedPreferences().contains(key)) {
            return getSharedPreferences().getString(key, DEFAULT_EMPTY_DATA);
        } else {
            return null;
        }
    }

    protected SharedPreferences getSharedPreferences() {
        return context.getSharedPreferences(PREFS_NAME, Application.MODE_PRIVATE);
    }
}
