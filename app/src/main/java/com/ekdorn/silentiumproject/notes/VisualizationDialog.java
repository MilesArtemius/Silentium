package com.ekdorn.silentiumproject.notes;

import android.content.Context;
import android.graphics.Color;
import android.hardware.Camera;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.ekdorn.silentiumproject.silent_core.Message;
import com.ekdorn.silentiumproject.silent_core.MorseListener;
import com.ekdorn.silentiumproject.silent_core.SilentFullScreenDialog;
import com.ekdorn.silentiumproject.silent_core.Visualizator;

import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;

/**
 * Created by User on 24.04.2017.
 */

public class VisualizationDialog extends SilentFullScreenDialog implements MediaPlayer.OnCompletionListener {

    private final static int LONG_SOUNDS_NUMBER = 2;
    private final static int SHORT_SOUNDS_NUMBER = 2;

    private ArrayList<Integer> mSounds;
    int currentTrack = 0;

    Camera camera;
    CameraManager camManager;
    Vibrator v;

    final AsyncTask<Void, Void, Void> vizual = new AsyncTask<Void, Void, Void>() {
        @Override
        protected Void doInBackground(Void... params) {
            Visualizer();
            return null;
        }
    };

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
        vizual.execute();
        Log.e("TAG", "onCreate: ");
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vizual.cancel(true);
                Log.e("TAG", "onTouchEvent: Interrupted");
                getDialog().dismiss();
            }
        });
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
            }
        }
    }

    public void Visualizer() {
        final Message msg = new Message(VisualMeaning, getContext());
        switch (getArguments().getString("lol")) {
                case "vibro":
                    v = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
                    for (Long lng : Visualizator.Patterna(msg.toString(), getContext())) {
                        Log.e("TAG", "doInBackground: " + lng);
                    }

                    v.vibrate(Visualizator.Patterna(msg.toString(), getContext()), -1);

                    getDialog().dismiss();
                    break;

                case "sound":
                    mSounds = new ArrayList<>();

                    ArrayList<Integer> ShortSounds = new ArrayList<>();
                    ArrayList<Integer> LongSounds = new ArrayList<>();
                    Random random = new Random();

                    for (int i = 0; i < SHORT_SOUNDS_NUMBER; i++) {
                        int j = getContext().getResources().getIdentifier("bell_" + i, "raw", getContext().getPackageName());
                        Log.e("TAG", "onViewCreated: " + j);
                        ShortSounds.add(j);
                    }

                    for (int i = 0; i < LONG_SOUNDS_NUMBER; i++) {
                        int j = getContext().getResources().getIdentifier("gong_" + i, "raw", getContext().getPackageName());
                        Log.e("TAG", "onViewCreated: " + j);
                        LongSounds.add(j);
                    }

                    for (Integer integer : msg.PatternCreator(msg.toString())) {
                        Log.e("TAG", "doInBackground: " + integer);
                        switch (integer) {
                            case 0:
                                break;
                            case 1:
                                mSounds.add(ShortSounds.get(random.nextInt(SHORT_SOUNDS_NUMBER)));
                                break;
                            case 2:
                                mSounds.add(LongSounds.get(random.nextInt(SHORT_SOUNDS_NUMBER)));
                                break;
                            case -1:
                                mSounds.add(-1);
                                break;
                            case -2:
                                mSounds.add(-2);
                                break;
                        }
                    }

                    MediaPlayer mp = MediaPlayer.create(getContext(), mSounds.get(0));
                    mp.setOnCompletionListener(this);
                    mp.start();
                    break;

                case ("backFlash"):
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        camManager = (CameraManager) getActivity().getSystemService(Context.CAMERA_SERVICE);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            camManager = (CameraManager) getActivity().getSystemService(Context.CAMERA_SERVICE);
                            try {
                                for (Integer integer : msg.PatternCreator(msg.toString())) {
                                    Log.e("TAG", "doInBackground: " + integer);
                                    switch (integer) {
                                        case 0:
                                            camManager.setTorchMode(camManager.getCameraIdList()[0], false);
                                            Thread.sleep((long) MorseListener.MESSAGE_LETTER_DURATION / 2);

                                            break;
                                        case 1:
                                            camManager.setTorchMode(camManager.getCameraIdList()[0], true);
                                            Thread.sleep((long) MorseListener.MESSAGE_LETTER_DURATION);
                                            break;
                                        case 2:
                                            camManager.setTorchMode(camManager.getCameraIdList()[0], true);
                                            Thread.sleep((long) MorseListener.MESSAGE_SPACE_DURATION);
                                            break;
                                        case -1:
                                            camManager.setTorchMode(camManager.getCameraIdList()[0], false);
                                            Thread.sleep((long) MorseListener.MESSAGE_SPACE_DURATION);
                                            break;
                                        case -2:
                                            camManager.setTorchMode(camManager.getCameraIdList()[0], false);
                                            Thread.sleep((long) MorseListener.MESSAGE_LETTER_DURATION);
                                            break;
                                    }
                                }
                                camManager.setTorchMode(camManager.getCameraIdList()[0], false);
                                getDialog().dismiss();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        camera = Camera.open();
                        Camera.Parameters parameters = camera.getParameters();
                        try {
                            for (Integer integer : msg.PatternCreator(msg.toString())) {
                                Log.e("TAG", "doInBackground: " + integer);
                                switch (integer) {
                                    case 0:
                                        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                                        camera.setParameters(parameters);
                                        camera.stopPreview();
                                        Thread.sleep((long) MorseListener.MESSAGE_LETTER_DURATION / 2);
                                        break;
                                    case 1:
                                        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                                        camera.setParameters(parameters);
                                        camera.startPreview();
                                        Thread.sleep((long) MorseListener.MESSAGE_LETTER_DURATION);
                                        break;
                                    case 2:
                                        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                                        camera.setParameters(parameters);
                                        camera.startPreview();
                                        Thread.sleep((long) MorseListener.MESSAGE_SPACE_DURATION);
                                        break;
                                    case -1:
                                        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                                        camera.setParameters(parameters);
                                        camera.stopPreview();
                                        Thread.sleep((long) MorseListener.MESSAGE_SPACE_DURATION);
                                        break;
                                    case -2:
                                        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                                        camera.setParameters(parameters);
                                        camera.stopPreview();
                                        Thread.sleep((long) MorseListener.MESSAGE_LETTER_DURATION);
                                        break;
                                }
                            }
                            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                            camera.setParameters(parameters);
                            camera.stopPreview();
                            getDialog().dismiss();
                        } catch (Exception e) {
                            e.fillInStackTrace();
                        }
                    }
                    break;
                case "FrontFlash":
                    Canvas canvas;
                    canvas = new Canvas();
                    Paint paint2 = new Paint();
                    paint2.setColor(Color.WHITE);
                    paint2.setStyle(Paint.Style.FILL);
                    canvas.drawPaint(paint2);
                    getView().draw(canvas);
            }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        OnComplete(mp);
    }

    public void OnComplete (MediaPlayer mp) {
        mp.release();
        currentTrack++;
        if (currentTrack < mSounds.size()) {
            if (mSounds.get(currentTrack) < 0) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                OnComplete(mp);
            } else {
                Log.e("TAG", "OnComplete: " + mSounds.get(currentTrack));
                try {
                    mp = MediaPlayer.create(getContext(), mSounds.get(currentTrack));
                    mp.setOnCompletionListener(this);
                    mp.start();
                } catch (Exception e) {
                    mp.release();
                }
            }
        } else {
            getDialog().dismiss();
        }
    }
}
