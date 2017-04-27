package com.ekdorn.silentiumproject.authentification;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.ekdorn.silentiumproject.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by User on 30.03.2017.
 */

public class Authentification extends AppCompatActivity {
    private String TAG = "LOLOLOLOLOLOLO";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.e(TAG, "onCreate: ");

        Log.e(TAG, "onCreate: ");

        EternalChoice();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        EternalChoice();
    }

    private void EternalChoice() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // User is signed in
            Log.e(TAG, "checkUser: " + user.getDisplayName());
            Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
            Intent intent = new Intent();
            setResult(RESULT_OK, intent);
            finish();
        } else {
            setContentView(R.layout.signin_primary);
            // User is signed out
            Log.d("TAG", "onAuthStateChanged:signed_out");
            Button button1 = (Button) findViewById(R.id.signin_button1);
            button1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), SignNewUserIn.class);
                    startActivityForResult(intent, 2);
                }
            });
            Button button2 = (Button) findViewById(R.id.signin_button2);
            button2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), LogExistingUserIn.class);
                    startActivityForResult(intent, 3);
                }
            });
        }
    }

    /*FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Name, email address, and profile photo Url
            String name = user.getDisplayName();
            String email = user.getEmail();
            Uri photoUrl = user.getPhotoUrl();

            // Check if user's email is verified
            boolean emailVerified = user.isEmailVerified();

            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getToken() instead.
            String uid = user.getUid();
        }


    }*/
}