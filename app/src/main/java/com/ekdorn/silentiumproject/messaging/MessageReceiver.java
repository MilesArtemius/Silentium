package com.ekdorn.silentiumproject.messaging;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.ekdorn.silentiumproject.MainActivity;
import com.ekdorn.silentiumproject.R;
import com.ekdorn.silentiumproject.input.SilentiumButton;
import com.ekdorn.silentiumproject.silent_core.Message;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.ArrayList;

import static android.support.v4.app.NotificationCompat.PRIORITY_MAX;
import static com.ekdorn.silentiumproject.silent_core.MorseListener.MESSAGE_END_DURATION;
import static com.ekdorn.silentiumproject.silent_core.MorseListener.MESSAGE_LETTER_DURATION;
import static com.ekdorn.silentiumproject.silent_core.MorseListener.MESSAGE_SPACE_DURATION;

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
        //Log.i("info", "From: " + remoteMessage.getFrom());
        //Log.i("info", "Notification Message Body: " + remoteMessage.getNotification().getBody());

        msg = new Message();

        //numMessages++;

        Log.e("TAG", "onMessageReceived: " + remoteMessage.getData().get("body"));
        Log.e("TAG", "onMessageReceived: " + msg.Decoder(remoteMessage.getData().get("body"), getApplicationContext()));
        Log.e("TAG", "onMessageReceived: " + remoteMessage.getData().get("title"));

        mBuilder = new NotificationCompat.Builder(this).setSmallIcon(R.mipmap.message_notificator)
                .setContentTitle(remoteMessage.getData().get("title"))
                .setContentText(msg.Decoder(remoteMessage.getData().get("body"), getApplicationContext()))
                //.setVibrate(Patterna(remoteMessage.getNotification().getBody()))
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setPriority(PRIORITY_MAX)
                .setAutoCancel(true)
                .setLargeIcon(SilentiumButton.getBitmapFromDrawable(getApplicationContext(), R.mipmap.ic_launcher));

        int mgC = (getApplicationContext().getSharedPreferences(getApplicationContext().getString(R.string.silent_preferences), Context.MODE_PRIVATE).getInt("messageCount", 0));
        Log.e("TAG", "onMessageReceived: " + mgC);

        SharedPreferences shprf = getApplicationContext().getSharedPreferences(getApplicationContext().getString(R.string.silent_preferences), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = shprf.edit();
        editor.putInt("messageCount", mgC + 1);
        editor.commit();

        if (mgC > 1) {
            mBuilder.setContentText("You've got some messages").setNumber(shprf.getInt("messageCount", 0));
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
    }
}