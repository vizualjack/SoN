package son.android;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;

public class Preferences {
    private final String FOLDER_KEY = "SYNC_FOLDER";

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
