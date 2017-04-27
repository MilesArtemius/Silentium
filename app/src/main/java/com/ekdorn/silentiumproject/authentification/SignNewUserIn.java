package com.ekdorn.silentiumproject.authentification;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ekdorn.silentiumproject.MainActivity;
import com.ekdorn.silentiumproject.R;
import com.ekdorn.silentiumproject.silent_core.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by User on 21.04.2017.
 */

public class SignNewUserIn extends AppCompatActivity {
    private String TAG = "LOLOLOLOLOLOLO";
    EditText SetName;
    EditText SetPassword;
    EditText SetConfirmation;
    String name;
    String password;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("users");
    DatabaseReference mySilentiumRef = database.getReference("message").child("Silentium").child("members");

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.signin_sign_in);

        SetName = (EditText) findViewById(R.id.setusername);
        SetPassword = (EditText) findViewById(R.id.setpassword);
        SetConfirmation = (EditText) findViewById(R.id.setconfirmation);
        Button SubmitButton = (Button) findViewById(R.id.submitbuttom);
        SubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = SetName.getText().toString();
                password = SetPassword.getText().toString();


                if (!password.equals(SetConfirmation.getText().toString())) {
                    Toast.makeText(SignNewUserIn.this, "Your passwords doesn't match", Toast.LENGTH_SHORT).show(); //password validity check
                } else if (name.indexOf('&') != 0) {
                    Toast.makeText(SignNewUserIn.this, "Your ID must start with '@'", Toast.LENGTH_SHORT).show(); //& start name check
                } else if (name.contains(".") || name.contains("#") || name.contains("$") || name.contains("[") || name.contains("]") || name.contains("@")) {
                    Toast.makeText(SignNewUserIn.this, "Your ID must not contain these weird symbols", Toast.LENGTH_SHORT).show(); //forbidden symbols check
                } else if (name.contains(" ")) {
                    Toast.makeText(SignNewUserIn.this, "Your ID must not contain any spaces", Toast.LENGTH_SHORT).show(); //space check
                } else if (name.length() <= 5) {
                    Toast.makeText(SignNewUserIn.this, "Your ID must be longer then 5 symbols", Toast.LENGTH_SHORT).show(); //ID length check
                } else if (password.length() <= 7) {
                    Toast.makeText(SignNewUserIn.this, "Your password must be longer then 7 symbols", Toast.LENGTH_SHORT).show(); //password length check

                } else {
                    try {
                        FirebaseAuth.getInstance().createUserWithEmailAndPassword(name + "@silentium.notspec", password).addOnCompleteListener(SignNewUserIn.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Log.d("TAG", "createUserWithEmail:onComplete:" + task.isSuccessful());
                                if (!task.isSuccessful()) {
                                    Toast.makeText(SignNewUserIn.this, "Authenification failed", Toast.LENGTH_SHORT).show();
                                } else {
                                    HashMap<String,String> arrayList = new HashMap<>();
                                    arrayList.put(UUID.randomUUID().toString(), FirebaseInstanceId.getInstance().getToken());
                                    HashMap<String,String> arrayList1 = new HashMap<>();
                                    arrayList1.put("0" + UUID.randomUUID().toString(), "Silentium");
                                    myRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(new User(name, name + "@silentium.notspec", arrayList, arrayList1));
                                    mySilentiumRef.child(UUID.randomUUID().toString()).setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());

                                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(SignNewUserIn.this);
                                    SharedPreferences.Editor editor = prefs.edit();
                                    editor.putString("CURRENT_USER_PASSWORD", password);
                                    editor.commit();

                                    Intent intent = new Intent();
                                    setResult(RESULT_OK, intent);
                                    finish();
                                }
                            }
                        });
                    } catch (Exception e) {
                        Log.e(TAG, "onClick: ", e);
                        Toast.makeText(SignNewUserIn.this, "Authentification failed", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

}