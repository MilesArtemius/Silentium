package com.ekdorn.silentiumproject.silent_statics;

import android.content.Context;

import com.ekdorn.silentiumproject.authentication.FireBaser;
import com.ekdorn.silentiumproject.silent_core.User;
import com.google.firebase.database.FirebaseDatabase;

public class Encryptor {
    public static String encrypt(String data) {
        return data;
    }

    public static void generateKeyPair (Context context) {
        String privateKey = "sassossosi";
        String publicKey = "sassossosi";
        Prefs.setString(context, Prefs.USER, Prefs.PRIVATE_KEY, privateKey);
        FireBaser.addParam(FirebaseDatabase.getInstance().getReference(FireBaser.USER_REF).child(Prefs.getUser(context, User.ID_REF)), User.KEY_REF, publicKey, null);
    }
}
