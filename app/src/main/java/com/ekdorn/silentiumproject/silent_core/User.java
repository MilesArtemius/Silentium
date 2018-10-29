package com.ekdorn.silentiumproject.silent_core;

import java.util.HashMap;

/**
 * Created by User on 02.04.2017.
 */

public class User {
    public static final String EMAIL_REF    = "email";
    public static final String UID_REF      = "uid";
    public static final String DEVICE_REF   = "device";
    public static final String DIALOGS_REF  = "dialogs";
    public static final String KEY_REF      = "key";
    public static final String SUBS_REF     = "subscriptions";

    public static final String ID_REF      = "id";

    public String device;
    public String uid;
    public HashMap<String, String> dialogs;
    public Info info;

    public User(String name, Object databaseUser) {
        HashMap<String, Object> user = (HashMap<String, Object>) databaseUser;

        this.info = new Info(name , (String) user.get(EMAIL_REF));
        this.dialogs = (HashMap<String, String>) user.get(DIALOGS_REF);
        this.device = (String) user.get(DEVICE_REF);
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + info.id + '\'' +
                ", email='" + info.email + '\'' +
                ", tokens=" + device +
                ", dialogs=" + dialogs +
                '}';
    }



    public static class Info {
        private String id;
        private String email;
        public Info(String id, String email, boolean isAdmin) {
            this.id = id;
            this.email = email;
        }
        public Info(String name, Object databaseUserInfo) {
            HashMap<String, Object> userInfo = (HashMap<String, Object>) databaseUserInfo;
            this.id = name;
            this.email = (String) userInfo.get(EMAIL_REF);
        }
        public String getId() {
            return id;
        }
        public String getEmail() {
            return email;
        }
    }
}