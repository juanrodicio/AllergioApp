package es.uca.allergioapp.POJOs;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceManager {

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context _context;

    // shared pref mode
    private int PRIVATE_MODE = 0;

    // Shared preferences file name
    private static final String PREF_NAME = "intro_allergio-welcome";

    private static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";

    public PreferenceManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setFirstTimeLaunch(boolean isFirstTime) {
        editor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime);
        editor.commit();
    }

    public boolean isFirstTimeLaunch() {
        return pref.getBoolean(IS_FIRST_TIME_LAUNCH, true);
    }

    public void setUsername(String username) {
        editor.putString("username", username);
        editor.commit();
    }

    public String getUsername() {
        return pref.getString("username", "");
    }

    public void setLogged(Boolean isLogged) {
        editor.putBoolean("logged", isLogged);
        editor.commit();
    }

    public Boolean isLogged() {
        return pref.getBoolean("logged", false);
    }
}