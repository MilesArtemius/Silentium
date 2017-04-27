package com.ekdorn.silentiumproject.notes;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.os.Vibrator;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.ekdorn.silentiumproject.R;
import com.ekdorn.silentiumproject.input.SilentiumButton;
import com.ekdorn.silentiumproject.silent_core.Message;
import com.ekdorn.silentiumproject.silent_core.MorseListener;
import com.ekdorn.silentiumproject.silent_core.SilentFullScreenDialog;
import com.ekdorn.silentiumproject.silent_core.Visualizator;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by User on 24.04.2017.
 */

public class VisualizationDialog extends SilentFullScreenDialog {

    private static final int START_DRAW_DARK = -1;
    private static final int START_DRAW_HOLLOW = 1;

    GraphicsView view;

    static String VisualMeaning;


    public static VisualizationDialog newInstance(String name, String type) {
        Bundle args = new Bundle();
        args.putString("lol", type);
        args.putSerializable("name", name);
        VisualizationDialog fragment = new VisualizationDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        VisualMeaning = getArguments().getString("name");
        view = new GraphicsView(getActivity());
        return view;
    }

    public class GraphicsView extends View {
        double rad = 1;
        double cosinus = 0;
        Paint paint = new Paint();

        public GraphicsView(Context context) {
            super(context);
        }
        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            paint.setARGB(100, 255, 108, 12);

            if (rad < Math.sqrt(((canvas.getWidth()/2) * (canvas.getWidth()/2)) + ((canvas.getHeight()/2) * (canvas.getHeight()/2)))) {
                canvas.drawCircle((canvas.getWidth() / 2), (canvas.getHeight() / 2), (float) rad, paint);
                rad += rad/2;

                canvas.drawCircle((canvas.getWidth() / 2), (canvas.getHeight() / 2), (float) rad, paint);
                //cosinus += (Math.PI / 2) / Math.sqrt(((canvas.getWidth()/2) * (canvas.getWidth()/2)) + ((canvas.getHeight()/2) * (canvas.getHeight()/2)));
                //rad = (Math.sin(cosinus)) * Math.sqrt(((canvas.getWidth()/2) * (canvas.getWidth()/2)) + ((canvas.getHeight()/2) * (canvas.getHeight()/2)));
                Log.e("TAG", "onDraw: " + rad );

                invalidate();
            } else {
                canvas.drawCircle((canvas.getWidth() / 2), (canvas.getHeight() / 2), (float) rad, paint);
            }
        }

        @Override
        public boolean onTouchEvent (MotionEvent event) {
            getDialog().dismiss();
            return false;
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        Vibrator v = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
        Message msg = new Message(VisualMeaning, getContext());
        for (Long lng : Visualizator.Patterna(msg.toString(), getContext())) {
            Log.e("TAG", "doInBackground: " + lng);
        }
        v.vibrate(Visualizator.Patterna(msg.toString(), getContext()), -1);

        getDialog().dismiss();

        super.onViewCreated(view, savedInstanceState);
    }
}
