package com.ekdorn.silentiumproject.silent_accessories;

import android.app.Dialog;
import android.content.Context;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.ekdorn.silentiumproject.R;
import com.ekdorn.silentiumproject.silent_core.Message;
import com.ekdorn.silentiumproject.silent_core.MorseListener;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by User on 24.04.2017.
 */

public class Visualizator implements MediaPlayer.OnCompletionListener {

    static Message msg;
    Context cntxt;
    Dialog dialog;
    String type;
    String VisualMeaning;

    private final static int LONG_SOUNDS_NUMBER = 2;
    private final static int SHORT_SOUNDS_NUMBER = 2;

    double MESSAGE_LETTER_DURATION;
    double MESSAGE_SPACE_DURATION;
    long MESSAGE_END_DURATION;

    private ArrayList<Integer> mSounds;
    int currentTrack = 0;
    boolean StopFlag = false;

    Camera camera;
    CameraManager camManager;
    Vibrator vi;


    public Visualizator(Context cntxt, String type, String VisualMeaning) {
        this.cntxt = cntxt;
        this.VisualMeaning = VisualMeaning;
        this.type = type;
        this.dialog = null;
    }

    public Visualizator(Context context, Dialog ThisDialog, String type, String VisualMeaning) {
        this.cntxt = context;
        this.dialog = ThisDialog;
        this.VisualMeaning = VisualMeaning;
        this.type = type;
    }

    public final AsyncTask<Void, Void, Void> vizual = new AsyncTask<Void, Void, Void>() {
        @Override
        protected Void doInBackground(Void... params) {
            Visualizer();
            return null;
        }
    };

    public void Visualizer() {

        try {
            MESSAGE_LETTER_DURATION = Double.parseDouble(PreferenceManager.getDefaultSharedPreferences(cntxt).getString("short_morse", "750"));
            MESSAGE_SPACE_DURATION = Double.parseDouble(PreferenceManager.getDefaultSharedPreferences(cntxt).getString("long_morse", "3000"));
            MESSAGE_END_DURATION = Long.parseLong(PreferenceManager.getDefaultSharedPreferences(cntxt).getString("frustration_morse", "5000"));
        } catch (NumberFormatException nfe) {
            //Toast.makeText(cntxt, cntxt.getString(R.string.wrong_morse_numerals_stayed), Toast.LENGTH_SHORT).show();
            MESSAGE_LETTER_DURATION = 750;
            MESSAGE_SPACE_DURATION = 3000;
            MESSAGE_END_DURATION = 5000;
        }

        final Message msg = new Message(VisualMeaning, cntxt);
        switch (type) {
            case "vibro":
                vi = (Vibrator) cntxt.getSystemService(Context.VIBRATOR_SERVICE);
                long OverAll = 0;

                ArrayList<Long> Pattern = new ArrayList<>();
                Pattern.add((long) 0);
                for (Integer intr: msg.PatternCreator(msg.toString())) {
                    switch (intr) {
                        case 0:
                            Pattern.add((long) MESSAGE_LETTER_DURATION / 2);
                            break;
                        case 1:
                            Pattern.add((long) MESSAGE_LETTER_DURATION);
                            break;
                        case 2:
                            Pattern.add((long) MESSAGE_SPACE_DURATION);
                            break;
                        case -1:
                            Pattern.add((long) MESSAGE_SPACE_DURATION);
                            break;
                        case -2:
                            Pattern.add((long) MESSAGE_LETTER_DURATION);
                            break;
                    }
                }
                Pattern.add(MESSAGE_END_DURATION);
                long [] Longer = new long [Pattern.size()];
                for (int i = 0; i < Pattern.size(); i++) {
                    Longer[i] = Pattern.get(i);
                    OverAll += Pattern.get(i);
                }

                vi.vibrate(Longer, -1);


                if (dialog != null) {
                    try {
                        Thread.sleep(OverAll);
                    } catch (InterruptedException e) {
                        vi.cancel();
                        e.printStackTrace();
                    }

                    dialog.dismiss();
                }
                break;

            case "sound":
                mSounds = new ArrayList<>();

                ArrayList<Integer> ShortSounds = new ArrayList<>();
                ArrayList<Integer> LongSounds = new ArrayList<>();
                Random random = new Random();

                for (int i = 0; i < SHORT_SOUNDS_NUMBER; i++) {
                    int j = cntxt.getResources().getIdentifier("bell_" + i, "raw", cntxt.getPackageName());
                    Log.e("TAG", "onViewCreated: " + j);
                    ShortSounds.add(j);
                }

                for (int i = 0; i < LONG_SOUNDS_NUMBER; i++) {
                    int j = cntxt.getResources().getIdentifier("gong_" + i, "raw", cntxt.getPackageName());
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

                MediaPlayer mp = MediaPlayer.create(cntxt, mSounds.get(0));
                mp.setOnCompletionListener(this);
                mp.start();
                break;

            case ("backFlash"):
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    camManager = (CameraManager) cntxt.getSystemService(Context.CAMERA_SERVICE);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        camManager = (CameraManager) cntxt.getSystemService(Context.CAMERA_SERVICE);
                        try {
                            for (Integer integer : msg.PatternCreator(msg.toString())) {
                                Log.e("TAG", "doInBackground: " + integer);
                                switch (integer) {
                                    case 0:
                                        camManager.setTorchMode(camManager.getCameraIdList()[0], false);
                                        Thread.sleep((long) MESSAGE_LETTER_DURATION / 2);

                                        break;
                                    case 1:
                                        camManager.setTorchMode(camManager.getCameraIdList()[0], true);
                                        Thread.sleep((long) MESSAGE_LETTER_DURATION);
                                        break;
                                    case 2:
                                        camManager.setTorchMode(camManager.getCameraIdList()[0], true);
                                        Thread.sleep((long) MESSAGE_SPACE_DURATION);
                                        break;
                                    case -1:
                                        camManager.setTorchMode(camManager.getCameraIdList()[0], false);
                                        Thread.sleep((long) MESSAGE_SPACE_DURATION);
                                        break;
                                    case -2:
                                        camManager.setTorchMode(camManager.getCameraIdList()[0], false);
                                        Thread.sleep((long) MESSAGE_LETTER_DURATION);
                                        break;
                                }
                            }
                            camManager.setTorchMode(camManager.getCameraIdList()[0], false);
                            if (dialog != null) {
                                dialog.dismiss();
                            }
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
                                    Thread.sleep((long) MESSAGE_LETTER_DURATION / 2);
                                    break;
                                case 1:
                                    parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                                    camera.setParameters(parameters);
                                    camera.startPreview();
                                    Thread.sleep((long) MESSAGE_LETTER_DURATION);
                                    break;
                                case 2:
                                    parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                                    camera.setParameters(parameters);
                                    camera.startPreview();
                                    Thread.sleep((long) MESSAGE_SPACE_DURATION);
                                    break;
                                case -1:
                                    parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                                    camera.setParameters(parameters);
                                    camera.stopPreview();
                                    Thread.sleep((long) MESSAGE_SPACE_DURATION);
                                    break;
                                case -2:
                                    parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                                    camera.setParameters(parameters);
                                    camera.stopPreview();
                                    Thread.sleep((long) MESSAGE_LETTER_DURATION);
                                    break;
                            }
                        }
                        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                        camera.setParameters(parameters);
                        camera.stopPreview();
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                    } catch (Exception e) {
                        e.fillInStackTrace();
                    }
                }
                break;
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        OnComplete(mp);
    }

    public void OnComplete (MediaPlayer mp) {
        mp.release();
        currentTrack++;
        if ((currentTrack < mSounds.size()) && (!StopFlag)) {
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
                    mp = MediaPlayer.create(cntxt, mSounds.get(currentTrack));
                    mp.setOnCompletionListener(this);
                    mp.start();
                } catch (Exception e) {
                    mp.release();
                }
            }
        } else {
            if (dialog != null) {
                dialog.dismiss();
            }
        }
    }
    
    
    public void Interrupt() {
        StopFlag = true;
        if (vi != null) {
            vi.cancel();
            Log.e("TAG", "Stopping vibrator");
        }
        if (!vizual.isCancelled()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                try {
                    camManager = (CameraManager) cntxt.getSystemService(Context.CAMERA_SERVICE);
                    camManager.setTorchMode(camManager.getCameraIdList()[0], false);
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                }
            }
            vizual.cancel(true);
        }
        Log.e("TAG", "onTouchEvent: Interrupted");
        if (dialog != null) {
            dialog.dismiss();
        }
    }
}
