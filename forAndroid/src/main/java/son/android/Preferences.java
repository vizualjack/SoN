package son.android;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;

public class Preferences {
    private final String FOLDER_KEY = "SYNC_FOLDER";
    private final String LOGGING_KEY = "FILE_LOGGING";

    private android.content.ContextWrapper contextWrapper;


    public Preferences(android.content.ContextWrapper contextWrapper) {
        this.contextWrapper = contextWrapper;
    }


    public String getBaseFolderPath() {
        return getStringPreference(FOLDER_KEY);
    }
    public void setBaseFolderPath(String newBaseFolderPath) {
        setStringPreference(FOLDER_KEY, newBaseFolderPath);
    }

    public boolean isFileLoggingActivated() {
        String boolText = getStringPreference(LOGGING_KEY);
        if(boolText == null) return false;
        return Boolean.parseBoolean(boolText);
    }
    public void setLoggingActivated(boolean newIsLoggingActivated) {
        setStringPreference(LOGGING_KEY, Boolean.toString(newIsLoggingActivated));
    }

    private String getStringPreference(String key) {
        return getAndroidSharedPreferences().getString(key, null);
    }
    private void setStringPreference(String key, String value) {
        SharedPreferences.Editor editor = getEditableAndroidSharedPreferences();
        editor.putString(key, value);
        editor.apply();
    }

    private SharedPreferences.Editor getEditableAndroidSharedPreferences() { return getAndroidSharedPreferences().edit(); }
    private android.content.SharedPreferences getAndroidSharedPreferences() { return contextWrapper.getSharedPreferences("MyAppPrefs", MODE_PRIVATE); }
}
