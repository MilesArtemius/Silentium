package com.ekdorn.silentiumproject.settings.settings_fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import com.ekdorn.silentiumproject.R;
import com.ekdorn.silentiumproject.authentification.Authentification;
import com.ekdorn.silentiumproject.settings.Settings;
import com.ekdorn.silentiumproject.settings.VibrationPreference;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by User on 04.05.2017.
 */

public class GeneralPreferenceFragment extends PreferenceFragment {

    private SharedPreferences.OnSharedPreferenceChangeListener prefListener;

    private EditTextPreference pref;
    private VibrationPreference pref1;
    private EditTextPreference pref2;
    private EditTextPreference pref3;

    FirebaseUser user;

    static String Name;
    String value;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_general);
        setHasOptionsMenu(true);

        GetName();
    }

    @Override
    public void onStart() {
        try {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.pref_header_general));
        } catch (Exception e) {
            e.fillInStackTrace();
        }

        user = FirebaseAuth.getInstance().getCurrentUser();

        pref = (EditTextPreference) findPreference("example_text");
        if (FirebaseAuth.getInstance().getCurrentUser().getDisplayName() == null) {
            pref.setSummary(Name);
        } else {
            pref.setSummary(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
        }
        Log.e("TAG", "onCreate: " + pref);

        pref1 = (VibrationPreference) findPreference("vibration_pattern_index");
        if (FirebaseAuth.getInstance().getCurrentUser().getEmail().contains("@silentium.notspec")) {
            pref1.setSummary(getString(R.string.not_specified));
        } else {
            pref1.setSummary(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        }
        Log.e("TAG", "onCreate: " + pref1);

        pref2 = (EditTextPreference) findPreference("example_password");
        pref2.setSummary("(" + getString(R.string.your_password) + ")");
        Log.e("TAG", "onCreate: " + pref2);

        pref3 = (EditTextPreference) findPreference("delete_user");
        pref3.setSummary(getString(R.string.deletion_measure));

        prefListener = new SharedPreferences.OnSharedPreferenceChangeListener(){
            public void onSharedPreferenceChanged(SharedPreferences prefs, String key){
                try {
                    Log.e("TAG", "onSharedPreferenceChanged: " + key + " " + prefs.getString(key, "l"));
                } catch (ClassCastException cce) {
                    //Log.e("TAG", "onSharedPreferenceChanged: " + key + " " + prefs.getBoolean(key, false));
                }
                switch (key) {
                    case "example_text":
                        value = prefs.getString(key, "Silly");
                        Log.e("TAG", "onSharedPreferenceChanged: " + value);
                        if (!value.equals("Silly")) {
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(value)
                                    .build();

                            user.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.e("TAG", "onSharedPreferenceChanged: " + FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
                                        Settings.Toaster(getString(R.string.profile_update_success));
                                    }
                                }
                            });
                        } else {
                            Settings.Toaster(getString(R.string.went_wrong));
                        }
                        break;
                    case "vibration_pattern_index":
                        Set<String> hashset = prefs.getStringSet(key, new HashSet<String>());
                        Log.e("TAG", "onSharedPreferenceChanged: " + hashset);
                        String password = "";

                        for (String str: hashset) {
                            if ((str.contains("@"))&&(str.contains("."))) {
                                value = str;
                            } else {
                                password = str;
                            }
                        }
                        Log.e("TAG", "Password is: " + password);
                        Log.e("TAG", "Email is: " + value);
                        if ((value == null)||(password.equals(""))) {
                            Settings.Toaster(getString(R.string.null_text));
                        } else {
                            AuthCredential credential = EmailAuthProvider.getCredential(FirebaseAuth.getInstance().getCurrentUser().getEmail(), password);
                            // Prompt the user to re-provide their sign-in credentials
                            user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d("TAG", "User re-authenticated.");
                                        user.updateEmail(value).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    FirebaseDatabase.getInstance().getReference("users").child(user.getUid()).child("Email").setValue(value);
                                                    Log.d("TAG", "User email address updated.");
                                                    Settings.Toaster(getString(R.string.email_update_success));
                                                    user.sendEmailVerification();
                                                } else {
                                                    Log.e("TAG", "onComplete: ", task.getException());
                                                    Settings.Toaster(getString(R.string.email_in_use));
                                                }
                                            }
                                        });
                                    } else {
                                        Settings.Toaster(getString(R.string.password_incorrect));
                                    }
                                }
                            });
                        }
                        break;
                    case "example_password":
                        value = prefs.getString(key, "@@@");
                        Log.e("TAG", "onSharedPreferenceChanged: " + value);
                        if (!value.equals("@@@")) {
                            AuthCredential credential = EmailAuthProvider.getCredential(FirebaseAuth.getInstance().getCurrentUser().getEmail(), value);
                            // Prompt the user to re-provide their sign-in credentials
                            user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d("TAG", "User re-authenticated.");
                                        FirebaseAuth.getInstance().sendPasswordResetEmail(FirebaseAuth.getInstance().getCurrentUser().getEmail());
                                        pref2.setSummary("(" + getString(R.string.your_password) + ")");
                                    } else {
                                        Settings.Toaster(getString(R.string.password_incorrect));
                                        pref2.setSummary("(" + getString(R.string.your_password) + ")");
                                    }
                                }
                            });
                        }  else {
                            Settings.Toaster(getString(R.string.went_wrong));
                        }
                        break;
                    case "delete_user":
                        value = prefs.getString(key, "@@@");
                        Log.e("TAG", "onSharedPreferenceChanged: " + value);
                        if (!value.equals("@@@")) {
                            AuthCredential credential = EmailAuthProvider.getCredential(FirebaseAuth.getInstance().getCurrentUser().getEmail(), value);
                            user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d("TAG", "User re-authenticated.");
                                        DatabaseReference myUserRef = FirebaseDatabase.getInstance().getReference("message");
                                        myUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                final HashMap<String, Object> value = (HashMap<String, Object>) dataSnapshot.getValue();
                                                for (String chatName : value.keySet()) {
                                                    if (chatName.contains(Name)) {
                                                        FirebaseDatabase.getInstance().getReference("message").child(chatName).setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                Log.e("TAG", "onComplete: 1/3 deleted");
                                                            }
                                                        });
                                                    }
                                                }
                                                for (final String uid : value.keySet()) {
                                                    HashMap<String, Object> value1 = (HashMap<String, Object>) value.get(uid);
                                                    Log.e("TAG", "onComplete: checking chat " + uid);
                                                    HashMap<String, String> value2 = (HashMap<String, String>) value1.get("members");
                                                    for (final String uuid : value2.keySet()) {
                                                        Log.e("TAG", "onComplete: checking user " + uuid);
                                                        if (value2.get(uuid).equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                                            FirebaseDatabase.getInstance().getReference("message").child(uid).child("members").child(uuid).setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()) {
                                                                        Log.e("TAG", "onComplete: MATCH FOUND " + uid);
                                                                    }
                                                                }
                                                            });
                                                        }
                                                    }
                                                    Log.e("TAG", "onComplete: 2/3 deleted");
                                                }
                                                FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        Settings.Toaster(getString(R.string.profile_delete_success));

                                                        PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().clear().commit();

                                                        FirebaseAuth.getInstance().getCurrentUser().delete();
                                                        FirebaseAuth.getInstance().signOut();
                                                        Intent intent = new Intent(getActivity(), Authentification.class);
                                                        getActivity().finish();
                                                        startActivity(intent);
                                                    }
                                                });
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {
                                                Log.w("TAG", "onCancelled: Some error occurs");
                                            }
                                        });
                                    } else {
                                        Settings.Toaster(getString(R.string.password_incorrect));
                                        pref3.setSummary(getString(R.string.deletion_measure));
                                    }
                                }
                            });
                        }  else {
                            Settings.Toaster(getString(R.string.went_wrong));
                        }
                        break;
                }
            }
        };

        PreferenceManager.getDefaultSharedPreferences(getActivity()).registerOnSharedPreferenceChangeListener(prefListener);

        Settings.bindPreferenceSummaryToValue(findPreference("example_text"));
        //TODO: needed or not: bindPreferenceSummaryToValue(findPreference("vibration_pattern_index"));
        Settings.bindPreferenceSummaryToValue(findPreference("example_password"));

        super.onStart();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            getActivity().onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void GetName() {
        FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Name = (String) dataSnapshot.getValue();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("TAG", "Failed to read value.", databaseError.toException());
            }
        });
    }
}
