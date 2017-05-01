package com.ekdorn.silentiumproject.messaging;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ekdorn.silentiumproject.R;
import com.ekdorn.silentiumproject.silent_core.Message;
//import com.ekdorn.silentiumproject.silent_core.Sent;
import com.ekdorn.silentiumproject.silent_accessories.SingleSilentiumOrInput;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.content.ContentValues.TAG;
import static com.ekdorn.silentiumproject.messaging.ContactPager.ARG_CRIME_ID;

/**
 * Created by User on 02.04.2017.
 */

public class MessageSender extends SingleSilentiumOrInput implements View.OnTouchListener {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("message");
    static FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    String str;
    String Name;

    public static final String LEGACY_SERVER_KEY = "AIzaSyD_5IDfyIWWIFcxFrk_jIUc3TNHgMvDkkI";
    List<String> regTokens;
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");


    public static MessageSender newInstance(String child, String name, String UrgentMessage) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_CRIME_ID, child);
        args.putSerializable("name", name);
        args.putSerializable("urgent", UrgentMessage);
        MessageSender fragment = new MessageSender();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        regTokens = new ArrayList<>();

        GetName();

        Log.e("TAG", "onClick: " + (String) getArguments().getSerializable("name"));
    }

    public void GetName() {
        database.getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Name = (String) dataSnapshot.getValue();

                Log.e(TAG, "NAME GOT: " + Name);

                try {
                    str = getArguments().getString("urgent");
                } catch (Exception e) {
                    str = "";
                    e.printStackTrace();
                }

                if (str != null) {
                    Log.e(TAG, "onCreate: " + getArguments().getString("urgent").equals(""));
                    SilentiumSender(new Message(getArguments().getString("urgent"), getContext()));
                }

                GetMessages();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("TAG", "Failed to read value.", databaseError.toException());
            }
        });
    }

    public void GetMessages() {
        myRef.child(getArguments().getString(ARG_CRIME_ID)).child("members").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e(TAG, "onDataChange: " + getArguments().getString(ARG_CRIME_ID));
                HashMap<String, String> value = (HashMap<String, String>) dataSnapshot.getValue();
                List<String> lst = new ArrayList<>(value.values());
                for (String i: lst) {
                    if (i.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                        Log.d(TAG, "onDataChange: One item excluded");
                        continue;
                    }
                    Log.e(TAG, "onDataChange: " + i);
                    database.getReference("users").child(i).child("Tokens").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            try {
                                HashMap<String, String> value1 = (HashMap<String, String>) dataSnapshot.getValue();
                                List<String> lst1 = new ArrayList<>(value1.values());
                                Log.e(TAG, "onDataChange: " + lst1);
                                regTokens.addAll(lst1);
                            } catch (ClassCastException cce) {
                                String value1 = (String) dataSnapshot.getValue();
                                Log.e(TAG, "onDataChange: " + value1);
                                regTokens.add(value1);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.w("TAG", "Failed to read value.", databaseError.toException());
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("TAG", "Failed to read value.", databaseError.toException());
            }
        });
    }

    @Override
    public String setButtonName() {
        return getString(R.string.button_for_message_sender);
    }

    @Override
    public String setStringName() {
        return getString(R.string.hint_for_message_sender);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void SilentiumSender(Message message) {
        Log.e("LALALALALALALALALAL", "Sender: " + regTokens.toString());
        Log.d(TAG, "doInBackground: " + message.toString());
        final String messageText = message.toString();
        final Message.Sent msg = new Message.Sent(user.getUid() + Name, String.valueOf(new Date().getTime()), message.toString());
        myRef.child(getArguments().getString(ARG_CRIME_ID)).child("messages").child(UUID.randomUUID().toString()).setValue(msg);
        for (final String RIF: regTokens) {
            new AsyncTask<Void,Void,Void>(){
                @Override
                protected Void doInBackground(Void... params) {
                    try {
                        OkHttpClient client = new OkHttpClient();
                        JSONObject json = new JSONObject();
                        JSONObject dataJson = new JSONObject();
                        dataJson.put("body", messageText);
                        Log.d(TAG, "doInBackground: " + getArguments().getSerializable("name"));
                        dataJson.put("title", (String) getArguments().getSerializable("name"));
                        dataJson.put("dialogName", getArguments().getString(ARG_CRIME_ID));
                        json.put("to", RIF);
                        json.put("data", dataJson);

                        Log.d(TAG, "doInBackground: " + json);

                        RequestBody body = RequestBody.create(JSON, json.toString());
                        Request request = new Request.Builder()
                                .header("Authorization","key="+ LEGACY_SERVER_KEY)
                                .url("https://fcm.googleapis.com/fcm/send")
                                .post(body)
                                .build();
                        Response response = client.newCall(request).execute();
                        String finalResponse = response.body().string();
                        Log.e("LALALALALALALALALAL", "Sender: " + finalResponse);
                    }catch (Exception e){
                        Log.d(TAG, e+"");
                    }
                    return null;
                }
            }.execute();
        }
    }
}