package com.ekdorn.silentiumproject.silent_accessories;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.ekdorn.silentiumproject.R;

/**
 * Created by User on 28.03.2017.
 */

public class SilentFullScreenDialog extends DialogFragment {
    Dialog dialog;
    Window window;
    GraphicsView view;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        dialog = new Dialog(getActivity());
        window = dialog.getWindow();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_message_choise);
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = new GraphicsView(getActivity());
        //view.setOnClickListener(setViewOnClick());
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
            Log.e("TAG", "onDraw: ");

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

                if (drawAlso(canvas)) {
                    invalidate();
                }
            }
        }

        @Override
        public boolean onTouchEvent (MotionEvent event) {
            setViewOnClick(event);
            return true;
        }
    }

    public boolean drawAlso(Canvas canvas){
        return false;
    }

    @Override
    public Dialog getDialog() {
        return dialog;
    }

    public void setViewOnClick(MotionEvent event) {
    }
}