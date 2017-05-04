package com.ekdorn.silentiumproject.authentification;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ekdorn.silentiumproject.R;
import com.ekdorn.silentiumproject.silent_core.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;
import java.util.UUID;

/**
 * Created by User on 21.04.2017.
 */

public class LogExistingUserIn extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    EditText SetName;
    EditText SetPassword;
    String email;
    String name;
    String password;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("users");
    DatabaseReference mySilentiumRef = database.getReference("message").child("Silentium").child("members");


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.signin_log_in);

        mAuth = FirebaseAuth.getInstance();

        email = "";

        SetName = (EditText) findViewById(R.id.getuid);
        SetName.setSelection(SetName.getText().length());
        SetPassword = (EditText) findViewById(R.id.getpassword);
        Button SubmitButton = (Button) findViewById(R.id.submittbutton1);
        SubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = SetName.getText().toString();
                password = SetPassword.getText().toString();
                getEmailByUID(name);
            }
        });
    }

    protected void getEmailByUID(final String uid) {
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap<String, Object> value = (HashMap<String, Object>) dataSnapshot.getValue();
                for (String str : value.keySet()) {
                    HashMap<String, Object> userDetails = (HashMap<String, Object>) value.get(str);
                    Log.e("TAG", "onDataChange: " + userDetails.get("Email").toString());
                    if (userDetails.get("Name").equals(uid)) {
                        email = (String) userDetails.get("Email");
                    }
                }

                try {
                    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(LogExistingUserIn.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Log.d("TAG", "signInWithEmail:onComplete:" + task.isSuccessful());
                                if (!task.isSuccessful()) {
                                    Log.w("TAG", "signInWithEmail:failed", task.getException());
                                    Toast.makeText(LogExistingUserIn.this, "Log in failed", Toast.LENGTH_SHORT).show();
                                } else {
                                    mySilentiumRef.child(UUID.randomUUID().toString()).setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());

                                    myRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Tokens").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            HashMap<String, String> tokens = (HashMap<String, String>) dataSnapshot.getValue();
                                            boolean exists = false;
                                            for (String tok: tokens.values()) {
                                                if (tok.equals(FirebaseInstanceId.getInstance().getToken())) {
                                                    exists = true;
                                                }
                                            }
                                            if (!exists) {
                                                myRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Tokens").child(UUID.randomUUID().toString()).setValue(FirebaseInstanceId.getInstance().getToken());
                                            }

                                            Intent intent = new Intent();
                                            setResult(RESULT_OK, intent);
                                            finish();
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                            Toast.makeText(getApplicationContext(), getString(R.string.went_wrong), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            } else {
                                Toast.makeText(getApplicationContext(), getString(R.string.password_incorrect), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } catch (Exception e) {
                    Log.e("TAG", "onClick: ", e);
                    Toast.makeText(LogExistingUserIn.this, getString(R.string.login_failed), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("TAG", "onCancelled: Some error occurs");
            }
        });
    }
}
