package com.ekdorn.silentiumproject.silent_accessories;

import android.content.Context;

import com.ekdorn.silentiumproject.silent_core.Message;

import java.util.ArrayList;

import static com.ekdorn.silentiumproject.silent_core.MorseListener.MESSAGE_END_DURATION;
import static com.ekdorn.silentiumproject.silent_core.MorseListener.MESSAGE_LETTER_DURATION;
import static com.ekdorn.silentiumproject.silent_core.MorseListener.MESSAGE_SPACE_DURATION;

/**
 * Created by User on 24.04.2017.
 */

public class Visualizator {

    static Message msg;

    public static long[] Patterna(String str, Context context) {

        msg = new Message(str, context);

        ArrayList<Long> Pattern = new ArrayList<>();
        Pattern.add((long) 0);
        for (Integer intr: msg.PatternCreator(str)) {
            //TODO: MESSAGE_SYMBOL_DURATION;
            switch (intr) {
                case 0:
                    Pattern.add((long) MESSAGE_LETTER_DURATION);
                    break;
                case 1:
                    Pattern.add((long) MESSAGE_LETTER_DURATION);
                    break;
                case 2:
                    Pattern.add((long) (MESSAGE_LETTER_DURATION * 2));
                    break;
                case -1:
                    Pattern.add((long) (MESSAGE_SPACE_DURATION));
                    break;
                case -2:
                    Pattern.add((long) (MESSAGE_LETTER_DURATION * 2));
                    break;
            }
        }
        Pattern.add(MESSAGE_END_DURATION);
        long [] Longer = new long [Pattern.size()];
        for (int i = 0; i < Pattern.size(); i++) {
            Longer[i] = Pattern.get(i);
        }
        return Longer;
    }

    public static long[][] BinaryPatterna(String str, Context context) {

        msg = new Message(str, context);

        Long Gap = 0L;
        ArrayList<Long[]> Pattern = new ArrayList<>();
        for (Integer intr: msg.PatternCreator(str)) {
            //TODO: MESSAGE_SYMBOL_DURATION;
            switch (intr) {
                case 1:
                    Pattern.add(new Long[] {Gap, (long) MESSAGE_LETTER_DURATION, (long) MESSAGE_LETTER_DURATION, 0L});
                    Gap = 0L;
                    break;
                case 2:
                    Pattern.add(new Long[] {Gap, (long) (MESSAGE_LETTER_DURATION * 2), (long) MESSAGE_LETTER_DURATION, 0L});
                    Gap = 0L;
                    break;
                case -1:
                    Gap = (long) (MESSAGE_SPACE_DURATION);
                    break;
                case -2:
                    Gap = (long) (MESSAGE_LETTER_DURATION * 2);
                    break;
            }
        }
        long [][] Longer = new long [Pattern.size()][4];
        for (int i = 0; i < Pattern.size(); i++) {
            for (int j = 0; j < 4; j++) {
                Longer[i][j] = Pattern.get(i)[j];
            }
        }
        return Longer;
    }
}
