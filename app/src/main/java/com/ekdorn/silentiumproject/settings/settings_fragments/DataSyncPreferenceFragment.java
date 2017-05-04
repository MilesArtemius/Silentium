package com.ekdorn.silentiumproject.settings.settings_fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.ekdorn.silentiumproject.R;
import com.ekdorn.silentiumproject.settings.Edit2TextPreference;
import com.ekdorn.silentiumproject.settings.Settings;

/**
 * Created by User on 04.05.2017.
 */

public class DataSyncPreferenceFragment extends PreferenceFragment {

    private SharedPreferences.OnSharedPreferenceChangeListener prefListener;

    String value;

    EditTextPreference pref1;
    EditTextPreference pref2;
    EditTextPreference pref3;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_data_sync);
        setHasOptionsMenu(true);
        setRetainInstance(true);
    }

    @Override
    public void onStart() {
        try {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.pref_header_data_sync));
        } catch (Exception e) {
            e.fillInStackTrace();
        }

        pref1 = (EditTextPreference) findPreference("short_morse");
        pref2 = (EditTextPreference) findPreference("long_morse");
        pref3 = (EditTextPreference) findPreference("frustration_morse");

        prefListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
                try {
                    value = prefs.getString(key, "0");
                    if (!TextUtils.isDigitsOnly(value)) {
                        Toast.makeText(Settings.getContext(), getString(R.string.any_number_wrong_format), Toast.LENGTH_SHORT).show();
                        switch (key) {
                            case "short_morse":
                                prefs.edit().putString(key, "750").apply();
                                pref1.setText("750");
                                pref1.setSummary("750");
                                break;
                            case "long_morse":
                                prefs.edit().putString(key, "3000").apply();
                                pref2.setText("3000");
                                pref2.setSummary("3000");
                                break;
                            case "frustration_morse":
                                prefs.edit().putString(key, "5000").apply();
                                pref3.setText("5000");
                                pref3.setSummary("5000");
                                break;
                        }
                    } else {
                        if (Double.parseDouble(value) <= 0) {
                            Toast.makeText(Settings.getContext(), getString(R.string.number_wrong_format), Toast.LENGTH_SHORT).show();
                            switch (key) {
                                case "short_morse":
                                    prefs.edit().putString(key, "750").apply();
                                    pref1.setText("750");
                                    pref1.setSummary("750");
                                    break;
                                case "long_morse":
                                    prefs.edit().putString(key, "3000").apply();
                                    pref2.setText("3000");
                                    pref2.setSummary("3000");
                                    break;
                                case "frustration_morse":
                                    prefs.edit().putString(key, "5000").apply();
                                    pref3.setText("5000");
                                    pref3.setSummary("5000");
                                    break;
                            }
                        }
                    }
                } catch (ClassCastException cce) {
                    cce.printStackTrace();
                }
            }
        };

        PreferenceManager.getDefaultSharedPreferences(getActivity()).registerOnSharedPreferenceChangeListener(prefListener);


        //bindPreferenceSummaryToValue(findPreference("sync_frequency"));

        Settings.bindPreferenceSummaryToValue(findPreference("short_morse"));
        Settings.bindPreferenceSummaryToValue(findPreference("long_morse"));
        Settings.bindPreferenceSummaryToValue(findPreference("frustration_morse"));

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