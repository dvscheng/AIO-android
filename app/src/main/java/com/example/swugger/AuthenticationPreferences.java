package com.example.swugger;

import android.content.Context;
import android.content.SharedPreferences;

/** Credits to https://gist.github.com/ranjithnair02/1c6dab7dec51971abfec */
public class AuthenticationPreferences {

    private static final String PREFERENCES_FILE_NAME = "ayioh_preferences";
    private static final String KEY_USERNAME = "KEY_USERNAME_AYIOH";
    private static final String KEY_TOKEN = "KEY_TOKEN_AYIOH";

    private SharedPreferences preferences;

    public AuthenticationPreferences(Context context) {
        preferences = context.getSharedPreferences(PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
    }

    public void setUsername(String username) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(KEY_USERNAME, username);
        editor.apply();
    }

    public void setToken(String token) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(KEY_TOKEN, token);
        editor.apply();
    }

    public String getUsername() {
        return preferences.getString(KEY_USERNAME, null);
    }

    public String getToken() {
        return preferences.getString(KEY_TOKEN, null);
    }
}
