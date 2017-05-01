package com.ekdorn.silentiumproject.notes;

import android.content.Context;
import android.hardware.Camera;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.util.Log;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.ekdorn.silentiumproject.silent_core.Message;
import com.ekdorn.silentiumproject.silent_core.MorseListener;
import com.ekdorn.silentiumproject.silent_accessories.SilentFullScreenDialog;
import com.ekdorn.silentiumproject.silent_accessories.Visualizator;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by User on 24.04.2017.
 */

public class VisualizationDialog extends SilentFullScreenDialog {

    Visualizator visl;

    public static VisualizationDialog newInstance(String name, String type) {
        Bundle args = new Bundle();
        args.putString("lol", type);
        args.putString("name", name);
        VisualizationDialog fragment = new VisualizationDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        visl = new Visualizator(getContext(), getDialog(), getArguments().getString("lol"), getArguments().getString("name"));
        visl.vizual.execute();
        Log.e("TAG", "onCreate: ");
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void setViewOnClick(MotionEvent event) {
        visl.Interrupt();
        Log.e("TAG", "onClick: Interrupt");
    }
}