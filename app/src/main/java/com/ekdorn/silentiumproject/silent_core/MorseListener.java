package com.ekdorn.silentiumproject.silent_core;

import android.content.Context;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;

import com.ekdorn.silentiumproject.R;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by User on 08.04.2017.
 */

public class MorseListener {
    long MESSAGE_END_DURATION;
    double MESSAGE_SPACE_DURATION;
    double MESSAGE_LETTER_DURATION;

    static long timeDown = 0;
    static long timeUp = 0;
    boolean flgDown = false;
    Message message = new Message();
    int symbol = 0;
    Timer timer;
    Context context;

    public MorseListener(Context context) {
        this.context = context;

        try {
            MESSAGE_LETTER_DURATION = Double.parseDouble(PreferenceManager.getDefaultSharedPreferences(context).getString("short_morse", "750"));
            MESSAGE_SPACE_DURATION = Double.parseDouble(PreferenceManager.getDefaultSharedPreferences(context).getString("long_morse", "3000"));
            MESSAGE_END_DURATION = Long.parseLong(PreferenceManager.getDefaultSharedPreferences(context).getString("frustration_morse", "5000"));
        } catch (NumberFormatException nfe) {
            Toast.makeText(context, context.getString(R.string.wrong_morse_numerals_stayed), Toast.LENGTH_SHORT).show();
            MESSAGE_LETTER_DURATION = 750;
            MESSAGE_SPACE_DURATION = 3000;
            MESSAGE_END_DURATION = 5000;
        }

        Log.e("TAG", "MorseListener: " + MESSAGE_LETTER_DURATION);
        Log.e("TAG", "MorseListener: " + MESSAGE_SPACE_DURATION);
        Log.e("TAG", "MorseListener: " + MESSAGE_END_DURATION);
        Log.e("TAG", "MorseListener: <--- end of the message --->");
    }

    public boolean deMorser(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (timer != null) {
                    timer.cancel();
                }

                if (!flgDown) {
                    Log.d("TM_UP", (System.currentTimeMillis() - timeUp) + "");
                    timeDown = System.currentTimeMillis();
                    flgDown = true;
                    Clicker1();

                    class Sent extends TimerTask {
                        public void run() {
                            Log.e("TAG", "run: " + symbol);

                            Obtainer1(message, symbol);

                            Sender(message);

                            symbol = 0;

                            Obtainer3(message);

                            timeDown = 0;
                            timeUp = 0;
                        }
                    }

                    timer = new Timer();
                    timer.schedule(new Sent(), MESSAGE_END_DURATION);

                    if (((timeDown - timeUp) > MESSAGE_LETTER_DURATION) && timeUp != 0) {
                        Log.d("Pack", "PACKED ");
                        Obtainer1(message, symbol);
                        symbol = 0;
                        if ((timeDown - timeUp) > MESSAGE_SPACE_DURATION) {
                            Log.d("SWAG", "lol");
                            Obtainer2(message);
                        }
                        timeUp = 0;
                    }


                    symbol *= 2;
                }
                return true;

            case MotionEvent.ACTION_UP:
                if (flgDown) {
                    Clicker2();
                    Log.d("TM_DOWN", (System.currentTimeMillis() - timeDown) + "");
                    if ((System.currentTimeMillis() - timeDown) < MESSAGE_LETTER_DURATION) {
                        symbol *= 2;
                        symbol++;
                    } else {
                        symbol *= 2;
                        symbol++;
                        symbol *= 2;
                        symbol++;
                    }

                    timeUp = System.currentTimeMillis();
                    flgDown = false;
                }

                return true;
        }
        return false;
    }

    public void Sender(Message message) {
    }

    public void Obtainer1(Message message, int symbol) {
        message.addSymb(symbol);
    }

    public void Obtainer2(Message message) {
        message.addSymb(-1);
    }

    public void Obtainer3(Message message) {
        message.clear();
    }

    public void Clicker1() {}

    public void Clicker2() {}
}
