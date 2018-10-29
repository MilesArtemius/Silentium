package com.ekdorn.silentiumproject.messaging;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MessageSender {
    public static final String SPECIAL = "special";

    public static final String PRIVATE_KEY_REQUEST = "private_key_request";
    public static final String PRIVATE_KEY_RESPONSE = "private_key_response";

    public static final String LEGACY_SERVER_KEY = "AIzaSyD_5IDfyIWWIFcxFrk_jIUc3TNHgMvDkkI";
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public static void sendSpecial(String type, String info, String addressee) {
        send(SPECIAL, type, info, addressee);
    }

    private static void send(final String mode, final String title, final String body, final String addressee) {
        new AsyncTask<Void,Void,Void>(){
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    OkHttpClient client = new OkHttpClient();
                    JSONObject json = new JSONObject();
                    JSONObject dataJson = new JSONObject();
                    dataJson.put("body", body);
                    dataJson.put("title", title);
                    dataJson.put("mode", mode);
                    json.put("to", addressee);
                    json.put("data", dataJson);

                    RequestBody body = RequestBody.create(JSON, json.toString());
                    Request request = new Request.Builder()
                            .header("Authorization","key="+ LEGACY_SERVER_KEY)
                            .url("https://fcm.googleapis.com/fcm/send")
                            .post(body)
                            .build();
                    Response response = client.newCall(request).execute();
                    String finalResponse = response.body().string();
                    Log.e("Message sent, ", "response: " + finalResponse);
                }catch (Exception e){
                    Log.d("TAG", e+"");
                }
                return null;
            }
        }.execute();
    }
}
