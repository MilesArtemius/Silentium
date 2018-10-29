package com.ekdorn.silentiumproject.authentication;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ekdorn.silentiumproject.R;
import com.ekdorn.silentiumproject.messaging.Subscriber;
import com.ekdorn.silentiumproject.silent_core.User;
import com.ekdorn.silentiumproject.silent_statics.Encryptor;
import com.ekdorn.silentiumproject.silent_statics.Prefs;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by User on 21.04.2017.
 */

public class SignNewUserIn extends AppCompatActivity {
    EditText inputName;
    EditText inputEmail;
    EditText inputPassword;
    EditText inputConfirmation;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signin_sign_in);

        inputName = (EditText) findViewById(R.id.setusername);
        inputEmail = (EditText) findViewById(R.id.setemail);
        inputPassword = (EditText) findViewById(R.id.setpassword);
        inputConfirmation = (EditText) findViewById(R.id.setconfirmation);
        Button SubmitButton = (Button) findViewById(R.id.submitbuttom);
        SubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = inputName.getText().toString();
                final String password = inputPassword.getText().toString();
                final String email = inputEmail.getText().toString();

                if (name.equals("")) {
                    inputName.setError("This field must be filled");
                } else if (email.equals("")) {
                    inputEmail.setError("Email is required");
                } else if (password.equals("")) {
                    inputPassword.setError("This field is empty!");
                } else if (!password.equals(inputConfirmation.getText().toString())) {
                    inputConfirmation.setError(getString(R.string.password_match_error)); //password validity check
                } else if (name.indexOf('&') != 0) {
                    inputName.setError(getString(R.string.password_ampersand_error)); //& start name check
                } else if (name.contains(".") || name.contains("#") || name.contains("$") || name.contains("[") || name.contains("]") || name.contains("@")) {
                    inputName.setError(getString(R.string.password_symbols_error)); //forbidden symbols check
                } else if (!email.contains("@") || !email.contains(".")) {
                    inputEmail.setError("Email malformed");
                } else if (name.contains(" ")) {
                    inputPassword.setError(getString(R.string.password_space_error)); //space check
                } else if (name.length() <= 5) {
                    inputName.setError(getString(R.string.password_length_error)); //ID length check
                } else if (password.length() <= 7) {
                    inputPassword.setError(getString(R.string.password_longer_error)); //password length check
                } else {
                    FireBaser.checkIfUserExists(name, new FireBaser.OnBooleanResult() {
                        @Override
                        public void onResult(boolean result) {
                            if (!result) {
                                authenticate(email, password, name);
                            } else {
                                inputName.setError("Already taken");
                            }
                        }
                    });
                }
            }
        });
    }

    private void authenticate(String email, String password, final String name) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d("TAG", "createUserWithEmail:onComplete:" + task.isSuccessful());
                if (!task.isSuccessful()) {

                    Toast.makeText(SignNewUserIn.this, getString(R.string.email_exists), Toast.LENGTH_SHORT).show();
                } else {

                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(name).build();
                    FirebaseAuth.getInstance().getCurrentUser().updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Prefs.registerNewUser(getApplicationContext());
                                FireBaser.addParam(FirebaseDatabase.getInstance().getReference(FireBaser.USER_REF).child(Prefs.getUser(getApplicationContext(), User.ID_REF)),
                                        User.DEVICE_REF, Prefs.getUser(getApplicationContext(), User.DEVICE_REF), null);

                                Encryptor.generateKeyPair(getApplicationContext());
                                FireBaser.updateUser(getApplicationContext());
                                Subscriber.subscribe(Subscriber.SILENTIUM_CHAT, new Subscriber.OnComplete() {
                                    @Override
                                    public void onComplete() {
                                        Toast.makeText(SignNewUserIn.this, "Joined Silentium chat", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent();
                                        setResult(RESULT_OK, intent);
                                        finish();
                                    }
                                });
                            }
                        }
                    });
                }
            }
        });
    }
}