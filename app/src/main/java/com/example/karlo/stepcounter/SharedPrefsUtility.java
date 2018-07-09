package com.example.karlo.stepcounter;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

public class SharedPrefsUtility {

    private static final String PREFS = "prefs";

    public static final String COUNT = "count";
    public static final String DAY_COUNTS = "day_count";
    public static final String DAY = "day";

    public static String getString(Context context, String key) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        return prefs.getString(key, "");
    }

    public static  void putString(Context context, String key, String value) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value).apply();
    }

    public static  int getInt(Context context, String key) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        return prefs.getInt(key, 0);
    }

    public static  void putInt(Context context, String key, int value) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(key, value).apply();
    }

    public static Set<String> getList(Context context, String key) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        return prefs.getStringSet(key, null);
    }

    public static void addToList(Context context, String key, String value) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Set<String> set = prefs.getStringSet(key, null);
        if (set == null) {
            set = new HashSet<>();
        }
        set.add(value);
        editor.putStringSet(key, set)
                .apply();
    }

    public static void remove(Context context, String key) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(key).apply();
    }

}
