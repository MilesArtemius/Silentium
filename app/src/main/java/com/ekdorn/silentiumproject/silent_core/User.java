package com.ekdorn.silentiumproject.silent_core;

import android.app.Dialog;
import android.util.Log;

import com.ekdorn.silentiumproject.messaging.DialogPager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by User on 02.04.2017.
 */

public class User {

    public String Name;
    public String Email;
    public boolean isAdmin;
    public HashMap<String, String> Tokens;
    public HashMap<String, String> Dialogs;

    public User(String name, String email, HashMap<String, String> tokens, HashMap<String, String> dialogs) {
        Name = name;
        Email = email;
        Tokens = tokens;
        Dialogs = dialogs;
    }

    public User(String name, HashMap<String, String> tokens, HashMap<String, String> dialogs) {
        Name = name;
        Tokens = tokens;
        Dialogs = dialogs;
        isAdmin = false;
    }

    public User(HashMap<String, String> dialogs, boolean bool) {
        Dialogs = dialogs;
        isAdmin = bool;
    }

    public User() {
    }

    public User(Object databaseUser) {
        HashMap<String, Object> user = (HashMap<String, Object>) databaseUser;
        ArrayList<Object> topLevelValues = new ArrayList<>(user.values());
        ArrayList<String> topLevelKeys = new ArrayList<>(user.keySet());
        for (int i = 0; i < topLevelKeys.size(); i++) {
            switch (topLevelKeys.get(i)) {
                case "Name":
                    this.Name = (String) topLevelValues.get(i);
                    break;
                case "Email":
                    this.Email = (String) topLevelValues.get(i);
                    break;
                case "isAdmin":
                    this.isAdmin = (Boolean) topLevelValues.get(i);
                    break;
                case "Tokens":
                    try {
                        this.Tokens = (HashMap<String, String>) topLevelValues.get(i);
                    } catch (Exception e) {
                        this.Tokens = new HashMap<String, String>();
                    }
                    break;
                case "Dialogs":
                    this.Dialogs = (HashMap<String, String>) topLevelValues.get(i);
                    break;
            }
        }
    }

    @Override
    public String toString() {
        return "User{" +
                "Name='" + Name + '\'' +
                ", Email='" + Email + '\'' +
                ", isAdmin=" + isAdmin +
                ", Tokens=" + Tokens +
                ", Dialogs=" + Dialogs +
                '}';
    }
}