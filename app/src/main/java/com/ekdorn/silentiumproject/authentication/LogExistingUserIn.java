package com.ekdorn.silentiumproject.authentication;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ekdorn.silentiumproject.R;
import com.ekdorn.silentiumproject.messaging.MessageSender;
import com.ekdorn.silentiumproject.silent_core.User;
import com.ekdorn.silentiumproject.silent_statics.Encryptor;
import com.ekdorn.silentiumproject.silent_statics.Prefs;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.lang.ref.WeakReference;

/**
 * Created by User on 21.04.2017.
 */

public class LogExistingUserIn extends AppCompatActivity {
    public AlertDialog transfer;
    public static WeakReference<LogExistingUserIn> activity;

    private EditText inputName;
    private EditText inputPassword;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signin_log_in);

        AlertDialog.Builder tb = new AlertDialog.Builder(LogExistingUserIn.this);
        transfer =  tb.create();
        activity = new WeakReference<LogExistingUserIn>(this);

        inputName = (EditText) findViewById(R.id.getuid);
        inputPassword = (EditText) findViewById(R.id.getpassword);
        Button SubmitButton = (Button) findViewById(R.id.submittbutton1);
        SubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = inputName.getText().toString();
                final String password = inputPassword.getText().toString();

                if (name.contains("@") && name.contains(".")) {

                    FireBaser.findIdByEmail(name, new FireBaser.OnStringResult() {
                        @Override
                        public void onResult(final String id) {
                            if (id != null) {
                                authenticate(id, name, password);
                            } else {
                                Toast.makeText(LogExistingUserIn.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {

                    FireBaser.findEmailById(name, new FireBaser.OnStringResult() {
                        @Override
                        public void onResult(final String email) {
                            if (email != null) {
                                authenticate(name, email, password);
                            } else {
                                Toast.makeText(LogExistingUserIn.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }

    public void authenticate(final String id, String email, String password) {
        Log.e("TAG", "authenticate: " + password + " " + email );
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).addOnCompleteListener(LogExistingUserIn.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    FireBaser.findTokenById(id, new FireBaser.OnStringResult() {
                        @Override
                        public void onResult(String token) {
                            MessageSender.sendSpecial(MessageSender.PRIVATE_KEY_REQUEST, Prefs.getUser(getApplicationContext(), User.DEVICE_REF), token);
                        }
                    });

                    transfer.setTitle("Transfer your encryption key");
                    transfer.setMessage("Check message on other device or press CANCEL to clear profile.\n\nWarning! Clicking Cancel will delete all dialogs and user data!");
                    transfer.setCancelable(false);
                    transfer.setButton(AlertDialog.BUTTON_POSITIVE, "Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switcher();
                            sublimeUser();
                            transfer.dismiss();
                        }
                    });
                    transfer.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            Intent intent = new Intent();
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                    });
                    transfer.show();
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.password_incorrect), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void switcher() {
        Prefs.registerNewUser(getApplicationContext());
        FireBaser.addParam(FirebaseDatabase.getInstance().getReference(FireBaser.USER_REF).child(Prefs.getUser(getApplicationContext(), User.ID_REF)),
                User.DEVICE_REF, Prefs.getUser(getApplicationContext(), User.DEVICE_REF), null);
    }

    public void sublimeUser() {
        FireBaser.clearDialogs(getApplicationContext());
        Encryptor.generateKeyPair(getApplicationContext());
    }

    @Override
    protected void onStop() {
        if (Prefs.getUser(getApplicationContext(), Prefs.PRIVATE_KEY).equals("")) {
            switcher();
            sublimeUser();
            transfer.dismiss();
        }
        super.onStop();
    }
}
