package com.ekdorn.silentiumproject.silent_statics;

import android.content.Context;
import android.content.SharedPreferences;

import com.ekdorn.silentiumproject.authentication.FireBaser;
import com.ekdorn.silentiumproject.silent_core.User;
import com.google.firebase.auth.FirebaseAuth;

public class Prefs {
    public static final String USER = "user";
    public static final String PRIVATE_KEY = "private_key";

    public static void setString(Context context, String prefsName, String argName, String value) {
        SharedPreferences sharedPrefs = context.getSharedPreferences(prefsName, Context.MODE_PRIVATE);
        sharedPrefs.edit().putString(argName, value).apply();
    }

    public static String getString(Context context, String prefsName, String argName) {
        SharedPreferences sharedPrefs = context.getSharedPreferences(prefsName, Context.MODE_PRIVATE);
        return sharedPrefs.getString(argName, "");
    }



    public static String getUser(Context context, String argName) {
        return getString(context, USER, argName);
    }

    public static void registerNewUser(Context context) {
        String id = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        setString(context, USER, User.ID_REF, id);
        setString(context, USER, User.EMAIL_REF, email);
        setString(context, USER, User.UID_REF, uid);
    }
}
