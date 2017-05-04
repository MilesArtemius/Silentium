package com.ekdorn.silentiumproject.settings.settings_fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.ekdorn.silentiumproject.R;
import com.ekdorn.silentiumproject.settings.Settings;

/**
 * Created by User on 04.05.2017.
 */

public class NotificationPreferenceFragment extends PreferenceFragment {

    private SharedPreferences.OnSharedPreferenceChangeListener prefListener;
    private SwitchPreference pref1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_notification);
        setHasOptionsMenu(true);

        pref1 = (SwitchPreference) findPreference("receive_in_morse");

        if (pref1.isChecked()) {
            pref1.setTitle(getString(R.string.receive_morse));
        } else {
            pref1.setTitle(getString(R.string.receive_letters));
        }

        prefListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
                if (key.equals("receive_in_morse")) {
                    if (pref1.isChecked()) {
                        pref1.setTitle(getString(R.string.receive_morse));
                    } else {
                        pref1.setTitle(getString(R.string.receive_letters));
                    }
                }
            }
        };

        PreferenceManager.getDefaultSharedPreferences(getActivity()).registerOnSharedPreferenceChangeListener(prefListener);

        Settings.bindPreferenceSummaryToValue(findPreference("notifications_new_message_ringtone"));
    }

    @Override
    public void onStart() {
        try {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.pref_header_notifications));
        } catch (Exception e) {
            e.fillInStackTrace();
        }
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
}
