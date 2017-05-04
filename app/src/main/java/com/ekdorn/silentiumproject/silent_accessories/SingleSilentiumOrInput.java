package com.ekdorn.silentiumproject.silent_accessories;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ekdorn.silentiumproject.R;
import com.ekdorn.silentiumproject.silent_core.Message;
import com.ekdorn.silentiumproject.silent_core.MorseListener;

import static android.content.ContentValues.TAG;

/**
 * Created by User on 17.04.2017.
 */

public class SingleSilentiumOrInput extends Fragment implements View.OnTouchListener {
    boolean state = true;

    ImageButton btn;
    ImageView iv;
    EditText ed;
    Button btn1;
    EditText ed2;
    RelativeLayout ll;
    MorseListener ML;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        ML = new MorseListener(getActivity()) {
            @Override
            public void Sender(Message message) {
                Log.d(TAG, "doInBackground: " + message.toString());
                SilentiumSender(message);
            }
        };

        ll = new RelativeLayout(getActivity());
        RelativeLayout.LayoutParams ViewLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        ll.setLayoutParams(ViewLayoutParams);
        ll.setId(R.id.listViewMain);

        btn1 = new Button(getActivity());
        RelativeLayout.LayoutParams params3 = new RelativeLayout.LayoutParams(200, ViewGroup.LayoutParams.WRAP_CONTENT);
        params3.addRule(RelativeLayout.ALIGN_PARENT_END);
        params3.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        btn1.setLayoutParams(params3);
        btn1.setText(setButtonName());
        btn1.setId(R.id.newbutton);

        iv = new ImageView(getActivity());
        RelativeLayout.LayoutParams imageViewParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, 300);
        imageViewParams.setMargins(5,5,5,5);
        iv.setLayoutParams(imageViewParams);
        iv.setImageResource(R.drawable.resource_big_logo);
        ll.addView(iv);
        iv.setOnTouchListener(this);

        btn = new ImageButton(getActivity());
        ViewGroup.LayoutParams imageViewLayoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        btn.setLayoutParams(imageViewLayoutParams);
        btn.setImageResource(R.drawable.ic_keyboard_black_24dp);
        btn.setId(R.id.imageview);
        ll.addView(btn);

        ed = new EditText(getActivity());
        RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params2.addRule(RelativeLayout.LEFT_OF, btn1.getId());
        params2.addRule(RelativeLayout.START_OF, btn1.getId());
        params2.addRule(RelativeLayout.RIGHT_OF, btn.getId());
        params2.addRule(RelativeLayout.END_OF, btn.getId());
        ed.setTextSize(btn1.getTextSize() / 2);
        ed.setHint(setStringName());
        ed.setLayoutParams(params2);
        ed.setId(R.id.messagetext);

        RelativeLayout.LayoutParams params8 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params8.addRule(RelativeLayout.LEFT_OF, btn1.getId());
        params8.addRule(RelativeLayout.START_OF, btn1.getId());
        params8.addRule(RelativeLayout.RIGHT_OF, btn.getId());
        params8.addRule(RelativeLayout.END_OF, btn.getId());
        params8.addRule(RelativeLayout.BELOW, ed.getId());
        ed2 = new EditText(getActivity());
        ed2.setSingleLine();
        ed2.setTextSize(btn1.getTextSize() / 2);
        ed2.setHint("Enter the Name of your chat");
        ed2.setLayoutParams(params8);

        super.onCreate(savedInstanceState);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (Math.sqrt((355 - event.getX()) * (355 - event.getX()) + (145 - event.getY()) * (145 - event.getY())) < 150) {
            return ML.deMorser(event);
        } else {
            Log.e(TAG, "onTouch: LOOOOOOOOOOOOOOL");
            return true;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.structure_content_main, container, false);
        RelativeLayout frame = (RelativeLayout) view.findViewById(R.id.fragmentContainer);
        frame.addView(CreateView());
        return view;
    }

    public RelativeLayout CreateView(){
        return ll;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        Log.e(TAG, "onViewCreated: CREATED");
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "onViewCreated: FIRST_BUTTON");
                if (state) {
                    ll.removeView(iv);
                    ll.addView(ed);
                    ll.addView(btn1);
                    btn.setImageResource(R.drawable.resource_small_logo);
                    state = false;
                } else {
                    ll.removeView(btn);
                    ll.addView(iv);
                    ll.addView(btn);
                    btn.setImageResource(R.drawable.ic_keyboard_black_24dp);
                    ll.removeView(ed);
                    ll.removeView(btn1);
                    state = true;
                }
            }
        });
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "doInBackgroundnbn: " +  ed.getText().toString());
                if (ed.getText().toString().length() > 0) {
                    SecondButtonOnClick(ed.getText().toString());
                    ed.setText("");    //Возможны ошибки....
                }
            }
        });
        ed.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.d(TAG, "doInBackgroundnb1: ");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d(TAG, "doInBackgroundnb2: ");
                TextChanged(ed);
                ed.requestFocus();
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.d(TAG, "doInBackgroundnb3: ");
            }
        });

        super.onViewCreated(view, savedInstanceState);
    }

    public String GetTheSecondEditTextValue() {
        return ed2.getText().toString();
    }

    public void SecondButtonOnClick(String string) {
        Log.d(TAG, "doInBackgroundnb: " + string);
        ML.Sender(new Message(string, getContext()));
    }

    public void SilentiumSender(Message message) {
    }

    public String setButtonName() {
        return "";
    }

    public String setStringName() {
        return "";
    }

    public void TextChanged(EditText editText) {
    }
}
