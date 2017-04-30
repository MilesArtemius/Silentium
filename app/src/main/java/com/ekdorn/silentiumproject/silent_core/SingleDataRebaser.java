package com.ekdorn.silentiumproject.silent_core;

import android.content.Context;
import android.content.SharedPreferences;

import com.ekdorn.silentiumproject.R;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by ALEXANDER on 1/28/2017.
 */

public class SingleDataRebaser<K,V> {

    public static HashMap<String, String> hashMap = new HashMap<String, String>();

    static {
        hashMap.put("1011", "a");
        hashMap.put("101101011", "ä");
        hashMap.put("11010101", "b");
        hashMap.put("110101101", "c");
        hashMap.put("11011011011", "ch");
        hashMap.put("110101", "d");
        hashMap.put("1", "e");
        hashMap.put("10101101", "f");
        hashMap.put("1101101", "g");
        hashMap.put("1010101", "h");
        hashMap.put("101", "i");
        hashMap.put("1011011011", "j");
        hashMap.put("1101011", "k");
        hashMap.put("10110101", "l");
        hashMap.put("11011", "m");
        hashMap.put("1101", "n");
        hashMap.put("11011011", "o");
        hashMap.put("1101101101", "ö");
        hashMap.put("101101101", "p");
        hashMap.put("1101101011", "q");
        hashMap.put("101101", "r");
        hashMap.put("10101", "s");
        hashMap.put("11", "t");
        hashMap.put("101011", "u");
        hashMap.put("101011011", "ü");
        hashMap.put("10101011", "v");
        hashMap.put("1011011", "w");
        hashMap.put("110101011", "x");
        hashMap.put("1101011011", "y");
        hashMap.put("110110101", "z");
        hashMap.put("1101101011011", "ñ");
        hashMap.put("1011011011011", "1");
        hashMap.put("101011011011", "2");
        hashMap.put("10101011011", "3");
        hashMap.put("1010101011", "4");
        hashMap.put("101010101", "5");
        hashMap.put("1101010101", "6");
        hashMap.put("11011010101", "7");
        hashMap.put("110110110101", "8");
        hashMap.put("1101101101101", "9");
        hashMap.put("11011011011011", "0");
        hashMap.put("10101010101", ".");
        hashMap.put("10110101101011", ",");
        hashMap.put("11011011010101", ":");
        hashMap.put("11010110101101", ";");
        hashMap.put("110101101101011", "(");
        hashMap.put("101101101101101", "'");
        hashMap.put("1011010101101", "[");
        hashMap.put("1101010101011", "-");
        hashMap.put("11010101101", "/");
        hashMap.put("1010110110101", "?");
        hashMap.put("110110101011011", "!");
        hashMap.put("11010101011", "/n");
        hashMap.put("101010101010101", "(error)");
        hashMap.put("10110110101101", "@");
        hashMap.put("10101101011", "(end)");
        hashMap.put("2", " ");
    }

    /*public String getSymbol(String code) {
        if (hashMap.containsKey(code)) {
            return hashMap.get(code);
        } else {
            return "<.>";
        }
    }

    public String getKeyByValue(String value) {
        for (Map.Entry<String, String> entry : hashMap.entrySet()) {
            if (entry.getValue().equals(value)) {
                return (entry.getKey());
            }
        }
        return "111";
    }*/

    public static void transferToPreferences(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.silent_preferences), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        editor.putBoolean("initialized", true);
        for (Map.Entry<String, String> entry : hashMap.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            editor.putString(key, value);
        }
        editor.apply();
    }
}
