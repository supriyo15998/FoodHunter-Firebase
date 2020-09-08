package com.example.foodhunter.sessions;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class LocalSessionStore {
    private SharedPreferences preferences;
    private SharedPreferences.Editor spEditor;
    private Context context;

    public LocalSessionStore(Context context) {
        this.context = context;
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }
    public void storeData(String key, String value) {
        spEditor = preferences.edit();
        spEditor.putString(key,value);
        spEditor.commit();
    }
    public String getData(String key) {
        return preferences.getString(key,"");
    }
    public void clearData() {
        spEditor = preferences.edit();
        spEditor.clear();
        spEditor.commit();
    }
}
