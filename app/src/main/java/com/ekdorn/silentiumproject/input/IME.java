package com.ekdorn.silentiumproject.input;

import android.content.Context;
import android.content.SharedPreferences;
import android.inputmethodservice.InputMethodService;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputConnection;
import android.widget.Button;
import android.widget.ImageView;

import com.ekdorn.silentiumproject.R;
import com.ekdorn.silentiumproject.silent_core.Message;
import com.ekdorn.silentiumproject.silent_core.MorseListener;
import com.ekdorn.silentiumproject.silent_accessories.SingleDataRebaser;

/**
 * Created by student1 on 30.01.17.
 */

public class IME extends InputMethodService {

    private MorseListener ML;
    private View keyboard;
    InputConnection ic;
    SingleDataRebaser sdr = new SingleDataRebaser();
    private SharedPreferences shprf;

    @Override
    public void onCreate() {
        super.onCreate();
        shprf = getApplicationContext().getSharedPreferences(getApplicationContext().getString(R.string.silent_preferences), Context.MODE_PRIVATE);
    }

    @Override
    public View onCreateInputView() {
        keyboard = (View) getLayoutInflater().inflate(R.layout.keyboard_button, null);
        ML = new MorseListener(getApplicationContext()) {
            @Override
            public void Sender(Message message) {
                ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER));
            }

            @Override
            public void Obtainer1(Message message, int symbol) {
                ic = getCurrentInputConnection();
                ic.commitText(shprf.getString(Integer.toBinaryString(symbol), "<.>"), 1);
            }

            @Override
            public void Obtainer2(Message message) {
                ic.commitText(" ", 1);
            }

            @Override
            public void Obtainer3(Message message) {
            }
        };

        ImageView logo = (ImageView) keyboard.findViewById(R.id.layout);

        Button button1 = (Button) keyboard.findViewById(R.id.button2);

        logo.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return ML.deMorser(event);
            }
        });

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ic = getCurrentInputConnection();
                ic.deleteSurroundingText(1, 0);
                Log.d("BUTTON", "button clicked");
            }
        });

        logo.setImageResource(R.drawable.resource_keyboard_logo);

        return keyboard;
    }
}