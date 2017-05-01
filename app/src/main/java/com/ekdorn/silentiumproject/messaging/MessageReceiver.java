package com.ekdorn.silentiumproject.messaging;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.ekdorn.silentiumproject.MainActivity;
import com.ekdorn.silentiumproject.R;
import com.ekdorn.silentiumproject.input.SilentiumButton;
import com.ekdorn.silentiumproject.silent_accessories.Visualizator;
import com.ekdorn.silentiumproject.silent_core.Message;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

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
    public void onMessageReceived(RemoteMessage remoteMessage) {

        msg = new Message();

        Log.e("TAG", "onMessageReceived: " + remoteMessage.getData().get("body"));
        Log.e("TAG", "onMessageReceived: " + msg.Decoder(remoteMessage.getData().get("body"), getApplicationContext()));
        Log.e("TAG", "onMessageReceived: " + remoteMessage.getData().get("title"));

        mBuilder = new NotificationCompat.Builder(this).setSmallIcon(R.mipmap.message_notificator)
                .setContentTitle(remoteMessage.getData().get("title"))
                .setContentText(msg.Decoder(remoteMessage.getData().get("body"), getApplicationContext()))
                .setPriority(PRIORITY_MAX)
                .setAutoCancel(true)
                .setLargeIcon(SilentiumButton.getBitmapFromDrawable(getApplicationContext(), R.mipmap.ic_launcher));

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