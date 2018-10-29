package com.ekdorn.silentiumproject.messaging;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.WindowManager;

import com.ekdorn.silentiumproject.MainActivity;
import com.ekdorn.silentiumproject.R;
import com.ekdorn.silentiumproject.authentication.FireBaser;
import com.ekdorn.silentiumproject.authentication.LogExistingUserIn;
import com.ekdorn.silentiumproject.input.SilentiumButton;
import com.ekdorn.silentiumproject.silent_accessories.Visualizator;
import com.ekdorn.silentiumproject.silent_core.Message;
import com.ekdorn.silentiumproject.silent_core.User;
import com.ekdorn.silentiumproject.silent_statics.Prefs;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Arrays;
import java.util.Collections;

import static android.support.v4.app.NotificationCompat.PRIORITY_MAX;

/**
 * Created by User on 06.04.2017.
 */

public class MessageReceiver extends FirebaseMessagingService {
    Message msg;
    static NotificationCompat.Builder mBuilder;

    static NotificationManager mNotifyMgr;

    int notifyID = 1;

    @Override
    public void onMessageReceived(final RemoteMessage remoteMessage) {

        Log.e("TAG", "onMessageReceived: " + Arrays.toString(remoteMessage.getData().values().toArray()));
        if (remoteMessage.getData().get("mode").equals("special")) {
            if (remoteMessage.getData().get("title").equals(MessageSender.PRIVATE_KEY_RESPONSE)) {
                if (remoteMessage.getData().get("body").equals("allow")) {
                    Prefs.setString(getApplicationContext(), Prefs.USER, "private_key", remoteMessage.getData().get("key"));
                    LogExistingUserIn.activity.get().switcher();
                    LogExistingUserIn.activity.get().transfer.dismiss();
                } else {
                    Prefs.setString(getApplicationContext(), Prefs.USER, "private_key", "");
                }
            } else if (remoteMessage.getData().get("title").equals(MessageSender.PRIVATE_KEY_REQUEST)) {

                System.out.println("LLLLLLLLLLLLLLLLLLLLLLLLLLLLLL");

                MainActivity.activity.get().showDialog(remoteMessage);
            }
        } else {
            msg = new Message();

            Log.e("TAG", "onMessageReceived: " + remoteMessage.getData().get("body"));
            Log.e("TAG", "onMessageReceived: " + msg.Decoder(remoteMessage.getData().get("body"), getApplicationContext()));
            Log.e("TAG", "onMessageReceived: " + remoteMessage.getData().get("title"));

            mBuilder = new NotificationCompat.Builder(this).setSmallIcon(R.mipmap.message_notificator)
                    .setContentTitle(remoteMessage.getData().get("title"))
                    .setContentText(msg.Decoder(remoteMessage.getData().get("body"), getApplicationContext()))
                    .setPriority(PRIORITY_MAX)
                    .setAutoCancel(true);

            try {
                mBuilder.setLargeIcon(SilentiumButton.getBitmapFromDrawable(getApplicationContext(), R.mipmap.ic_launcher));
            } catch (NullPointerException npe) {
                npe.fillInStackTrace();
            }

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            Boolean enabled = preferences.getBoolean("is_custom_notifications_enabled", false);
            Boolean vibration = preferences.getBoolean("custom_vibration", false);
            Boolean sound = preferences.getBoolean("custom_sound", false);
            Boolean flash = preferences.getBoolean("custom_light", false);
            Boolean encoded = preferences.getBoolean("receive_in_morse", false);


            if (encoded) {
                mBuilder.setContentText(msg.MorseDecoder(remoteMessage.getData().get("body")));
            }

            if (!sound) {
                mBuilder.setSound(Settings.System.DEFAULT_NOTIFICATION_URI);
            }


            int mgC = preferences.getInt("messageCount", 0);
            Log.e("TAG", "onMessageReceived: " + mgC);

            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt("messageCount", mgC + 1);
            editor.commit();

            if (mgC > 1) {
                mBuilder.setContentText(getString(R.string.messages_got)).setNumber(preferences.getInt("messageCount", 0));
            }

            // Creates an explicit intent for an Activity in your app
            Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);

            resultIntent.putExtra("DialogName", remoteMessage.getData().get("dialogName"));
            resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);


            //if (numMessages > 1) {
            //    mBuilder.setContentText("You've got some new messages ").setNumber(numMessages);
            //}


            // The stack builder object will contain an artificial back stack for the
            // started Activity.
            // This ensures that navigating backward from the Activity leads out of
            // your application to the Home screen.
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
            // Adds the back stack for the Intent (but not the Intent itself)
            stackBuilder.addParentStack(MainActivity.class);
            // Adds the Intent that starts the Activity to the top of the stack
            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

            //PendingIntent resultPendingIntent = PendingIntent.getActivity(getApplicationContext(), 1, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(resultPendingIntent);
            // mId allows you to update the notification later on.
            mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            // Builds the notification and issues it.
            mNotifyMgr.notify(notifyID, mBuilder.build());


            if (enabled && vibration) {
                final Visualizator visl = new Visualizator(getApplicationContext(), "vibro", msg.Decoder(remoteMessage.getData().get("body"), getApplicationContext()));
                Thread soundThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        visl.Visualizer();
                    }
                });
                soundThread.run();
            }
            if (enabled && sound) {
                final Visualizator visl = new Visualizator(getApplicationContext(), "sound", msg.Decoder(remoteMessage.getData().get("body"), getApplicationContext()));
                Thread soundThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        visl.Visualizer();
                    }
                });
                soundThread.run();
            }
            if (enabled && flash) {
                final Visualizator visl = new Visualizator(getApplicationContext(), "backFlash", msg.Decoder(remoteMessage.getData().get("body"), getApplicationContext()));
                Thread soundThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        visl.Visualizer();
                    }
                });
                soundThread.run();
            }
        }
    }


    @Override
    public void onNewToken(String token) {
        Log.d("TAG", "Refreshed token: " + token);
        FireBaser.addParam(FirebaseDatabase.getInstance().getReference(FireBaser.USER_REF).child(Prefs.getUser(getApplicationContext(), User.ID_REF)),
                User.DEVICE_REF, Prefs.getUser(getApplicationContext(), User.DEVICE_REF), null);
        Prefs.setString(getApplicationContext(), Prefs.USER, User.DEVICE_REF, token);
    }
}