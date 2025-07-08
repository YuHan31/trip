package com.xr.traveltracker.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefManager {
    private static final String SHARED_PREF_NAME = "MyAppPrefs";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USERNAME = "username";

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public SharedPrefManager(Context context) {
        sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void saveLoginStatus(boolean isLoggedIn) {
        editor.putBoolean(KEY_IS_LOGGED_IN, isLoggedIn);
        editor.apply();
    }

    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public void saveUsername(String username) {
        editor.putString(KEY_USERNAME, username);
        editor.apply();
    }

    public String getUsername() {
        return sharedPreferences.getString(KEY_USERNAME, null);
    }

    public void clear() {
        editor.clear();
        editor.apply();
    }
}