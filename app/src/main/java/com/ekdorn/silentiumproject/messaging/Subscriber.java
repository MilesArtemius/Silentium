package com.ekdorn.silentiumproject.messaging;

import android.support.annotation.NonNull;

import com.ekdorn.silentiumproject.authentication.FireBaser;
import com.ekdorn.silentiumproject.silent_core.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

public class Subscriber {
    public static final String SILENTIUM_CHAT = "Silentium";

    public static void subscribe(final String topic, final OnComplete complete) {
        FirebaseMessaging.getInstance().subscribeToTopic(SILENTIUM_CHAT).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                FireBaser.addParam(FirebaseDatabase.getInstance().getReference(FireBaser.USER_REF)
                        .child(FirebaseAuth.getInstance().getCurrentUser().getDisplayName())
                        .child(User.SUBS_REF), null, topic, new FireBaser.OnVoidResult() {
                    @Override
                    public void onResult() {
                        complete.onComplete();
                    }
                });
            }
        });
    }

    public interface OnComplete {
        void onComplete();
    }
}
