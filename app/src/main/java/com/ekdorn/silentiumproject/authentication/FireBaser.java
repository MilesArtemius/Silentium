package com.ekdorn.silentiumproject.authentication;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.ekdorn.silentiumproject.silent_core.User;
import com.ekdorn.silentiumproject.silent_statics.Prefs;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;
import java.util.UUID;

public class FireBaser {
    public static final String USER_REF = "users";

    // SIGN OUT:

    public static void signOut(final OnVoidResult result) { //TODO: add transfer dialog!
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myUserRef = database.getReference(USER_REF).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("tokens");
        myUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                HashMap<String, String> value = (HashMap<String, String>) dataSnapshot.getValue();
                for (final String uid: value.keySet()) {
                    if (value.get(uid).equals(FirebaseInstanceId.getInstance().getToken())) {
                        myUserRef.child(uid).setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.e("TAG", "onComplete: deleted " + uid);
                                }
                            }
                        });
                    }
                }
                result.onResult();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("TAG", "onCancelled: Some error occurs");
            }
        });
    }

    // SEARCHING DATA:

    public static void checkIfUserExists(String id, final OnBooleanResult result) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        database.getReference(USER_REF).orderByKey().equalTo(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if ((!dataSnapshot.exists()) || (dataSnapshot.getChildrenCount() == 0)) {
                    result.onResult(false);
                } else {
                    result.onResult(true);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public static void findIdByEmail(String email, final OnStringResult result) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        database.getReference(USER_REF).orderByChild(User.EMAIL_REF).equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if ((!dataSnapshot.exists()) || (dataSnapshot.getChildrenCount() == 0)) {
                    result.onResult("");
                } else {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        result.onResult(postSnapshot.getKey());
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }

    public static void findEmailById(String id, final OnStringResult result) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        database.getReference(USER_REF).orderByKey().equalTo(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if ((!dataSnapshot.exists()) || (dataSnapshot.getChildrenCount() == 0)) {
                    result.onResult("");
                } else {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        result.onResult(postSnapshot.child(User.EMAIL_REF).getValue(String.class));
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }

    public static void findTokenById(String id, final OnStringResult result) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        database.getReference(USER_REF).orderByKey().equalTo(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if ((!dataSnapshot.exists()) || (dataSnapshot.getChildrenCount() == 0)) {
                    result.onResult("");
                } else {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        result.onResult(postSnapshot.child(User.DEVICE_REF).getValue(String.class));
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }

    // WORKING WITH USER DATA:

    public static void updateUser(Context context) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        try {
            database.getReference(USER_REF).child(Prefs.getUser(context, User.ID_REF)).child(User.EMAIL_REF).setValue(Prefs.getUser(context, User.EMAIL_REF));
            database.getReference(USER_REF).child(Prefs.getUser(context, User.ID_REF)).child(User.UID_REF).setValue(Prefs.getUser(context, User.UID_REF));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void addParam(DatabaseReference ref, @Nullable String key, Object param, @Nullable final OnVoidResult result) {
        if (key == null) {
            key = UUID.randomUUID().toString();
        }
        try {
            ref.child(key).setValue(param).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (result != null) result.onResult();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void clearDialogs(Context context) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        HashMap<String,String> dialogs = new HashMap<>();
        try {
            database.getReference(USER_REF).child(Prefs.getUser(context, User.ID_REF)).child(User.DIALOGS_REF).setValue(dialogs);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public interface OnVoidResult {
        void onResult();
    }

    public interface OnStringResult {
        void onResult(String result);
    }

    public interface OnBooleanResult {
        void onResult(boolean result);
    }
}
