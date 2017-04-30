package com.ekdorn.silentiumproject.input;

/**
 * Created by User on 20.03.2017.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.ekdorn.silentiumproject.silent_core.Message;
import com.ekdorn.silentiumproject.silent_core.MorseListener;
import com.ekdorn.silentiumproject.R;
import com.ekdorn.silentiumproject.notes.NoteDBHelper;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SilentiumButton extends Fragment {

    NoteDBHelper DBH;
    Context context;
    MorseListener ML;

    Bitmap bitmap;

    GraphicsView view;
    static int pointMeasure;
    static final int REDRAW_OVERALL_TIME = 4;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Silentium");

        context = getContext();

        bitmap = getBitmapFromDrawable(context, R.drawable.resource_big_logo);

        pointMeasure = 0;

        ML = new MorseListener(getContext()) {
            @Override
            public void Sender(Message message) {
                //FragmentManager manager = getFragmentManager();
                //SelectionDialog dialog = new SelectionDialog();
                //dialog.show(manager, "LOL");

                SimpleDateFormat DF = new SimpleDateFormat("EEEE, MMMM dd, yyyy");
                //mDateTextView.setText(DF.format(mCrime.CreateDate));
                DBH = new NoteDBHelper(context);
                DBH.addRec("Title", message.toAnotherString(getContext()), new Date().toString());
                //Log.e("JSONNER", new Message().new Sent("author", new Date().toString()).toJSON());
            }

            @Override
            public void Clicker1() {
                pointMeasure = REDRAW_OVERALL_TIME;
                Log.e("TAG", "onDraw: Added");
                view.invalidate();
            }

            @Override
            public void Clicker2() {
                pointMeasure = 0;
                Log.e("TAG", "onDraw: Impressed");
            }
        };
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = new GraphicsView(getActivity());
        return view;
    }

    public class GraphicsView extends View {
        float rad;
        Paint paint = new Paint();

        public GraphicsView(Context context) {
            super(context);
        }
        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            if (pointMeasure == 0) {
                rad = ((canvas.getWidth() < canvas.getHeight()) ? (canvas.getWidth() / 5 * 2) : (canvas.getHeight() / 5 * 2));
                canvas.drawBitmap(Bitmap.createScaledBitmap(bitmap, (int) rad*2, (int) rad*2, false), canvas.getWidth()/2 - rad, canvas.getHeight()/2 - rad, paint);
            } else {
                if ((rad < (canvas.getWidth() / 40 * 17)) && (rad < (canvas.getHeight() / 40 * 17))) {
                    Log.e("TAG", "onDraw: Invalidated " + new Date().getTime());
                    rad += pointMeasure;
                }
                canvas.drawBitmap(Bitmap.createScaledBitmap(bitmap, (int) rad * 2, (int) rad * 2, false), canvas.getWidth() / 2 - rad, canvas.getHeight() / 2 - rad, paint);
                invalidate();
            }
        }

        @Override
        public boolean onTouchEvent (MotionEvent event) {
            if (Math.sqrt((getWidth()/2 - event.getX()) * (getWidth()/2 - event.getX()) + (getHeight()/2 - event.getY()) * (getHeight()/2 - event.getY())) < rad) {
                return ML.deMorser(event);
            } else {
                return false;
            }
        }
    }

    @Override
    public void onDestroy() {
        bitmap.recycle();
        super.onDestroy();
    }

    public static Bitmap getBitmapFromDrawable(Context context, @DrawableRes int drawableId) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableId);

        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        } else if (drawable instanceof VectorDrawable || drawable instanceof VectorDrawableCompat) {
            Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);

            return bitmap;
        } else {
            throw new IllegalArgumentException("unsupported drawable type");
        }
    }
}

            /*<item name="ic_menu_camera" type="drawable">@android:drawable/ic_menu_camera</item>
    <item name="ic_menu_gallery" type="drawable">@android:drawable/ic_menu_gallery</item>
    <item name="ic_menu_slideshow" type="drawable">@android:drawable/ic_menu_slideshow</item>
    <item name="ic_menu_manage" type="drawable">@android:drawable/ic_menu_manage</item>
    <item name="ic_menu_share" type="drawable">@android:drawable/ic_menu_share</item>
    <item name="ic_menu_send" type="drawable">@android:drawable/ic_menu_send</item>*/