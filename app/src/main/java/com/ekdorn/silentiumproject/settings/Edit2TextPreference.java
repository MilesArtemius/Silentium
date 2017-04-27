package com.ekdorn.silentiumproject.settings;

import android.content.Context;
import android.preference.EditTextPreference;
import android.util.AttributeSet;

/**
 * Created by User on 24.04.2017.
 */

public class Edit2TextPreference extends EditTextPreference{

    public Edit2TextPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public Edit2TextPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public Edit2TextPreference(Context context) {
        super(context);
    }
}
