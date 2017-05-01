package com.ekdorn.silentiumproject.settings;

/**
 * Created by User on 23.04.2017.
 */

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.os.Build;
import android.preference.DialogPreference;
import android.preference.EditTextPreference;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.ekdorn.silentiumproject.R;
import com.ekdorn.silentiumproject.messaging.DialogPager;

import java.util.HashSet;

public class VibrationPreference extends DialogPreference {

    private View view;
    EditText password;
    EditText email;
    Context cntxt;

    public VibrationPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        cntxt = context;
    }

    @Override
    protected void onPrepareDialogBuilder(Builder builder) {
        super.onPrepareDialogBuilder(builder);

        LayoutInflater inflater = (LayoutInflater) cntxt.getSystemService (Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.dialog_email_reset, null);

        builder.setView(view);

        builder.setMessage(cntxt.getString(R.string.email_change_message));
        builder.setPositiveButton(cntxt.getString(R.string.email_change_positive), this).setNegativeButton(cntxt.getString(R.string.email_change_negative), this);

        password = (EditText) view.findViewById(R.id.password_reset);
        email = (EditText) view.findViewById(R.id.email_reset);

        password.setEnabled(true);
        email.setEnabled(true);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        super.onClick(dialog, which);
        if (which == DialogInterface.BUTTON_POSITIVE) {
            HashSet<String> output = new HashSet<>();
            output.add(password.getText().toString());
            output.add(email.getText().toString());
            PreferenceManager.getDefaultSharedPreferences(getContext()).edit().putStringSet(getKey(), output).commit();
        }
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

    }
}