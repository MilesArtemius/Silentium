package com.ekdorn.silentiumproject.settings;


import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.ekdorn.silentiumproject.R;
import com.ekdorn.silentiumproject.authentification.Authentification;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class Settings extends AppCompatPreferenceActivity {

    static String Name;
    //static String password;

    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();

            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                // Set the summary to reflect the new value.
                preference.setSummary(
                        index >= 0
                                ? listPreference.getEntries()[index]
                                : null);

            } else if (preference instanceof RingtonePreference) {
                // For ringtone preferences, look up the correct display value
                // using RingtoneManager.
                if (TextUtils.isEmpty(stringValue)) {
                    // Empty values correspond to 'silent' (no ringtone).
                    preference.setSummary(R.string.pref_ringtone_silent);

                } else {
                    Ringtone ringtone = RingtoneManager.getRingtone(
                            preference.getContext(), Uri.parse(stringValue));

                    if (ringtone == null) {
                        // Clear the summary if there was a lookup error.
                        preference.setSummary(null);
                    } else {
                        // Set the summary to reflect the new ringtone display
                        // name.
                        String name = ringtone.getTitle(preference.getContext());
                        preference.setSummary(name);
                    }
                }

            } else {
                // For all other preferences, set the summary to the value's
                // simple string representation.
                preference.setSummary(stringValue);
            }
            return true;
        }
    };

    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();
        GetName();
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

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
        return super.onCreateView(parent, name, context, attrs);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.pref_headers, target);
    }

    /**
     * This method stops fragment injection in malicious applications.
     * Make sure to deny any unknown fragments here.
     */
    protected boolean isValidFragment(String fragmentName) {
        return PreferenceFragment.class.getName().equals(fragmentName)
                || GeneralPreferenceFragment.class.getName().equals(fragmentName)
                || DataSyncPreferenceFragment.class.getName().equals(fragmentName)
                || NotificationPreferenceFragment.class.getName().equals(fragmentName);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        menu.findItem(R.id.action_settings).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.menu_signout:
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), Authentification.class);
                startActivityForResult(intent, 2);
                return true;
            case android.R.id.home:
                this.onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }








    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class GeneralPreferenceFragment extends PreferenceFragment {

        private SharedPreferences.OnSharedPreferenceChangeListener prefListener;

        private EditTextPreference pref;
        private EditTextPreference pref1;
        private EditTextPreference pref2;
        private VibrationPreference pref3;

        FirebaseUser user;

        String value;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_general);
            setHasOptionsMenu(true);

            user = FirebaseAuth.getInstance().getCurrentUser();

            pref = (EditTextPreference) findPreference("example_text");
            if (FirebaseAuth.getInstance().getCurrentUser().getDisplayName() == null) {
                pref.setText(Name);
            } else {
                pref.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
            }
            Log.e("TAG", "onCreate: " + pref);

            /*pref1 = (EditTextPreference) findPreference("example_email");
            if (FirebaseAuth.getInstance().getCurrentUser().getEmail().contains("@silentium.notspec")) {
                pref1.setText("Not yet specified");
            } else {
                pref1.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());
            }
            Log.e("TAG", "onCreate: " + pref1);*/

            pref2 = (EditTextPreference) findPreference("example_password");
            pref2.setText("(your password)");
            Log.e("TAG", "onCreate: " + pref2);

            pref3 = (VibrationPreference) findPreference("vibration_pattern_index");
            //TODO: revise;
            //pref3.setText("e-mail");
            Log.e("TAG", "onCreate: " + pref3);

            //PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().putBoolean("vibration_pattern_index", false).commit();

            //-- preference change listener
            prefListener = new SharedPreferences.OnSharedPreferenceChangeListener(){
                public void onSharedPreferenceChanged(SharedPreferences prefs, String key){
                    try {
                        Log.e("TAG", "onSharedPreferenceChanged: " + key + " " + prefs.getString(key, "lolk)"));
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
                                            Toast.makeText(getActivity(), "Profile information was updated", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            } else {
                                Toast.makeText(getActivity(), "Some inner error occurs", Toast.LENGTH_SHORT).show();
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
                            Log.e("TAG", "Password is: " + hashset);
                            if ((value == null)||(password == null)) {
                                Log.d("TAG", "onSharedPreferenceChanged: Values were null");
                            } else {
                                if ((!value.equals("Silly")) && (!password.equals("@@@"))) {
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
                                                            Toast.makeText(getActivity(), "E-mail updated", Toast.LENGTH_SHORT).show();
                                                            user.sendEmailVerification();
                                                        } else {
                                                            Log.e("TAG", "onComplete: ", task.getException());
                                                            Toast.makeText(getActivity(), "This e-mail is already in use", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                            } else {
                                                Toast.makeText(getActivity(), "The password is incorrect", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                } else {
                                    Toast.makeText(getActivity(), "Some inner error occurs", Toast.LENGTH_SHORT).show();
                                }
                            }
                            break;
                        case "example_password":
                            value = prefs.getString(key, "Silly");
                            Log.e("TAG", "onSharedPreferenceChanged: " + value);
                            if (!value.equals("@@@")) {
                                AuthCredential credential = EmailAuthProvider.getCredential(FirebaseAuth.getInstance().getCurrentUser().getEmail(), value);
                                // Prompt the user to re-provide their sign-in credentials
                                user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Log.d("TAG", "User re-authenticated.");
                                        FirebaseAuth.getInstance().sendPasswordResetEmail(FirebaseAuth.getInstance().getCurrentUser().getEmail());
                                    }
                                });
                            }  else {
                                Toast.makeText(getActivity(), "Some inner error occurs", Toast.LENGTH_SHORT).show();
                            }
                            break;
                        /*case "" :
                            if (PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean(key, false)&&(!password.equals("@@@"))) {
                                AuthCredential credential = EmailAuthProvider.getCredential(FirebaseAuth.getInstance().getCurrentUser().getEmail(), password);
                                // Prompt the user to re-provide their sign-in credentials
                                user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Log.d("TAG", "User re-authenticated.");
                                        FirebaseAuth.getInstance().sendPasswordResetEmail(FirebaseAuth.getInstance().getCurrentUser().getEmail());
                                    }
                                });
                            }
                            PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().putBoolean(key, false).commit();*/
                    }
                }
            };

            PreferenceManager.getDefaultSharedPreferences(getActivity()).registerOnSharedPreferenceChangeListener(prefListener);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            //
            bindPreferenceSummaryToValue(findPreference("example_text"));
            //bindPreferenceSummaryToValue(findPreference("example_list"));
            //TODO: needed or not: bindPreferenceSummaryToValue(findPreference("vibration_pattern_index"));
            bindPreferenceSummaryToValue(findPreference("example_password"));
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                getActivity().onBackPressed();
                //startActivity(new Intent(getActivity(), Settings.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }

    /**
     * This fragment shows notification preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class NotificationPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_notification);
            setHasOptionsMenu(true);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue(findPreference("notifications_new_message_ringtone"));
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                getActivity().onBackPressed();
                //startActivity(new Intent(getActivity(), Settings.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }

    /**
     * This fragment shows data and sync preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class DataSyncPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_data_sync);
            setHasOptionsMenu(true);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue(findPreference("sync_frequency"));
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                getActivity().onBackPressed();
                //startActivity(new Intent(getActivity(), Settings.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }


}