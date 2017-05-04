package com.ekdorn.silentiumproject.silent_core;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.ekdorn.silentiumproject.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ALEXANDER on 1/11/2017.
 */

public class Message {
    private SharedPreferences shprf;

    private List<Integer> Msg;

    public void addSymb(int a) {
        Msg.add(a);
    }

    public Message() {
        Msg = new ArrayList<>();
    }

    public Message(String text, Context context) {
        Msg = new ArrayList<>();
        Map<String,?> keys = context.getSharedPreferences(context.getString(R.string.silent_preferences), Context.MODE_PRIVATE).getAll();
        for (char ch: text.toLowerCase().toCharArray()) {
            boolean isUnSupported = false;
            try {
                for(Map.Entry<String,?> entry: keys.entrySet()){
                    //Log.d("TAG", "Message: " + entry.getValue());
                    //Log.d("TAG", "Message: " + ch);
                    if (entry.getValue().equals(String.valueOf(ch))) {
                        this.Msg.add(Integer.parseInt(entry.getKey(), 2));
                        isUnSupported = true;
                    }
                }
            } catch (NumberFormatException nfe) {
                this.Msg.add(-1);
                isUnSupported = true;
            }
            if (!isUnSupported) {
                Toast.makeText(context, context.getString(R.string.unsupported_input), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void clear() {
        this.Msg.clear();
    }

    public String toAnotherString(Context context) {
        shprf = context.getSharedPreferences(context.getString(R.string.silent_preferences), Context.MODE_PRIVATE);
        String s = "";
        for (Integer i : Msg) {
            if (i == -1) {
                s += " ";
            } else {
                String h = Integer.toBinaryString(i);
                Log.e("ABORT", h);
                s += shprf.getString(h, "<.>");
            }
        }
        Log.e("TAG", "toAnotherString: " + s);
        return s;
    }

    public String Decoder(String str, Context context) {
        shprf = context.getSharedPreferences(context.getString(R.string.silent_preferences), Context.MODE_PRIVATE);
        String string = "";
        for (int i = 0; i < str.toCharArray().length; i++) {
            if (str.toCharArray()[i] == ' ') {
                string += " ";
            } else {
                String h = Integer.toBinaryString((int) str.toCharArray()[i]);
                Log.e("ABORT", h);
                Log.e("LOG", shprf.getString(h, "<.>"));
                string += shprf.getString(h, "<.>");
            }
        }
        return string;
    }

    public String MorseDecoder(String str) {
        String string = "";
        for (int i = 0; i < str.toCharArray().length; i++) {
            if (str.toCharArray()[i] == ' ') {
                string += " ";
            } else {
                String h = Integer.toBinaryString((int) str.toCharArray()[i]);
                string += h;
            }
        }
        return string;
    }

    public ArrayList<Integer> PatternCreator(String str) {
        ArrayList<Integer> Pattern = new ArrayList<>();
        for (int i = 0; i < str.toCharArray().length; i++) {
            if (str.toCharArray()[i] == ' ') {
                Pattern.add(-1);
            } else {
                boolean isDouble = false;
                if ((i != 0) && (Pattern.get(Pattern.size() - 1) != -1)) {
                    Pattern.add(-2);
                }
                String h = Integer.toBinaryString((int) str.toCharArray()[i]);
                for (char ch: h.toCharArray()) {
                    if (ch == '1') {
                        if (isDouble) {
                            Pattern.remove(Pattern.size() - 1);
                            Pattern.add(2);
                            isDouble = false;
                        } else {
                            Pattern.add(1);
                            isDouble = true;
                        }
                    } else {
                        Pattern.add(0);
                        isDouble = false;
                    }
                }
            }
        }
        String string = "";
        for (Integer intr: Pattern) {
            string += intr.toString();
        }
        Log.e("TAG", "PatternCreator: " + string);
        return Pattern;
    }

    @Override
    public String toString() {
        StringBuilder lst = new StringBuilder();
        for (Integer i : Msg) {
            if (i == -1) {
                lst.append(' ');
            } else {
                char g = (char) (int) i;
                lst.append(g);
            }
        }
        Log.e("TAG", "toString: " + Msg.toString());
        Log.e("TAG", "toString: " + lst.toString());
        return lst.toString();
    }






    public class Note extends Message {
        public String Title;
        public String Text;
        public String CreateDate;

        /*public Note(String title, String createDate) {
            Title = title;
            CreateDate = createDate;
            Text = this.toAnotherString();
        }*/
        public Note() {
        }
        /*public String toAnotherString() {
            String s = "";
            for (Integer i : Msg) {
                if (i == -1) {
                    s += " ";
                } else {
                    String h = Integer.toBinaryString(i);
                    Log.e("ABORT", h);
                    s += sdr.getSymbol(h);
                }
            }
            Log.e("TAG", "toAnotherString: " + s);
            return s;
        }*/
    }

    public static class Sent extends Message {
        public String Author;
        public String Text;
        public String Date;

        public Sent(HashMap<String, Object> data, Context context) {
            Message msg = new Message();
            List<Object> list = new ArrayList<>(data.values());
            List<String> keylist = new ArrayList<> (data.keySet());

            for (int j = 0; j < keylist.size(); j++) {
                if (keylist.get(j).equals("Author")) {
                    this.Author = (String) list.get(j);
                } else if (keylist.get(j).equals("Text")) {
                    this.Text = msg.Decoder((String) list.get(j), context);
                } else if (keylist.get(j).equals("Date")) {
                    this.Date = (String) list.get(j);
                }
            }
        }

        public Sent(String title, String createDate, String text) {
            Author = title;
            Date = createDate;
            Text = text;
        }

        public Sent() {}

        /*public String toJSON(){
            JSONObject jsonObject= new JSONObject();
            try {
                jsonObject.put("author", this.Author);
                jsonObject.put("text", this.Text);
                jsonObject.put("date", this.Date);
                return jsonObject.toString();
            } catch (JSONException e) {
                // Auto-generated catch block
                e.printStackTrace();
                return "";
            }
        }*/
    }
}