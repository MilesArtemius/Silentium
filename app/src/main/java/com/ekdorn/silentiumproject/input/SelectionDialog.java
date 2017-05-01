package com.ekdorn.silentiumproject.input;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.MotionEvent;

import com.ekdorn.silentiumproject.R;
import com.ekdorn.silentiumproject.messaging.DialogPager;
import com.ekdorn.silentiumproject.notes.NoteDBHelper;
import com.ekdorn.silentiumproject.notes.NotePager;
import com.ekdorn.silentiumproject.silent_accessories.SilentFullScreenDialog;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by User on 28.03.2017.
 */

public class SelectionDialog extends SilentFullScreenDialog {

    static int walkThrough = 0;
    Canvas sizeCanvas;
    NoteDBHelper DBH;

    static float posYDown;
    static float posXDown;

    Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public static SelectionDialog newInstance(String text) {
        Bundle args = new Bundle();
        args.putString("text", text);
        SelectionDialog fragment = new SelectionDialog();
        fragment.setArguments(args);
        return fragment;
    }

    public Path drawArrow(Canvas canvas, int walkThrough, boolean isHirizontal) {

        Path path = new Path();
        if (isHirizontal) {
            path.moveTo(((canvas.getWidth() / 2) - (canvas.getWidth() / 14) * walkThrough), ((canvas.getHeight() / 2) - (canvas.getHeight() / 24) * walkThrough));
            path.lineTo(((canvas.getWidth() / 2) - (canvas.getWidth() / 7) * walkThrough), (canvas.getHeight() / 2));
            path.lineTo(((canvas.getWidth() / 2) - (canvas.getWidth() / 14) * walkThrough), ((canvas.getHeight() / 2) + (canvas.getHeight() / 24) * walkThrough));
        } else {
            path.moveTo(((canvas.getWidth() / 2) - (canvas.getWidth() / 24) * walkThrough), ((canvas.getHeight() / 2) - (canvas.getHeight() / 14) * walkThrough));
            path.lineTo((canvas.getWidth() / 2), ((canvas.getHeight() / 2) - (canvas.getHeight() / 7) * walkThrough));
            path.lineTo(((canvas.getWidth() / 2) + (canvas.getWidth() / 24) * walkThrough), ((canvas.getHeight() / 2) - (canvas.getHeight() / 14) * walkThrough));
        }

        return path;
    }

    @Override
    public boolean drawAlso(Canvas canvas) {
        sizeCanvas = canvas;

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth((canvas.getWidth() > canvas.getHeight()) ? (canvas.getHeight() / 64) : (canvas.getWidth() / 64));
        paint.setARGB(200, 255, 108, 12);
        paint.setAntiAlias(true);

        walkThrough += ((walkThrough < 3) ? (1) : (-2));

        Log.e("TAG", "drawAlso: " + walkThrough);

        canvas.drawPath(drawArrow(canvas, walkThrough, true), paint);
        canvas.drawPath(drawArrow(canvas, -walkThrough, true), paint);
        canvas.drawPath(drawArrow(canvas, walkThrough, false), paint);
        canvas.drawPath(drawArrow(canvas, -walkThrough, false), paint);

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return true;
    }

    @Override
    public void setViewOnClick(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                posYDown = event.getY();
                posXDown = event.getX();
                Log.e("TAG", "setViewOnClick: GATHERED: X " + posXDown + " and Y " + posYDown);
                break;
            case MotionEvent.ACTION_UP:
                Log.e("TAG", "setViewOnClick: DISMISSED " + event.getX() + " " + event.getY());
                if (Math.abs(posXDown - event.getX()) > (sizeCanvas.getWidth() / 2)) {
                    getDialog().dismiss();
                } else {
                    if ((posYDown - event.getY()) > (sizeCanvas.getHeight() / 2)) {
                        Log.e("TAG", "setViewOnClick: CHECKED");
                        FragmentManager fm = getActivity().getSupportFragmentManager();
                        if (!fm.getFragments().contains(new DialogPager())) {
                            fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                            fm.beginTransaction().addToBackStack(null).replace(R.id.fragmentContainer, DialogPager.newInstance(getArguments().getString("text"))).commit();
                            Log.e("TAG", "setViewOnClick: " + getArguments().getString("text"));
                        }
                        getDialog().dismiss();
                    }
                    if ((posYDown - event.getY()) < -(sizeCanvas.getHeight() / 2)) {
                        SimpleDateFormat DF = new SimpleDateFormat("EEEE, MMMM dd, yyyy");
                        DBH = new NoteDBHelper(getContext());
                        DBH.addRec("Title", getArguments().getString("text"), DF.format(new Date()));
                        Log.e("TAG", "setViewOnClick: SAVED");
                        FragmentManager fm = getActivity().getSupportFragmentManager();
                        if (!fm.getFragments().contains(new NotePager())) {
                            fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                            fm.beginTransaction().addToBackStack(null).replace(R.id.fragmentContainer, new NotePager()).commit();
                        }
                        getDialog().dismiss();
                    }
                }
                break;
        }
    }
}