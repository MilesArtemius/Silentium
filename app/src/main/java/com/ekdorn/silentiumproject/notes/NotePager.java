package com.ekdorn.silentiumproject.notes;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.ListPopupWindow;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ekdorn.silentiumproject.MainActivity;
import com.ekdorn.silentiumproject.input.SelectionDialog;
import com.ekdorn.silentiumproject.messaging.ContactCreate;
import com.ekdorn.silentiumproject.silent_accessories.SingleSilentiumOrInput;
import com.ekdorn.silentiumproject.silent_core.Message;
import com.ekdorn.silentiumproject.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NotePager extends SingleSilentiumOrInput {

    AlertDialog altdlg;

    private RecyclerView mCrimeRecyclerView;
    private CrimeAdapter mAdapter;
    private int TouchPosition;

    ListPopupWindow popup;
    ListAdapter adapter;
    ArrayList<String> popUpList;

    RelativeLayout parental;

    static String VisualMeaning;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        parental = CreateView();

        popUpList = new ArrayList<>();
        popUpList.add(getString(R.string.visualize));
        popup = new ListPopupWindow(getContext());
        popup.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String s = popUpList.get(position);
                if (s.equals(getString(R.string.visualize))) {
                    setDialog(VisualMeaning);
                }
                popup.dismiss();
            }
        });
        adapter = new ArrayAdapter<>(getActivity(), R.layout.support_simple_spinner_dropdown_item, popUpList);

        mCrimeRecyclerView = new RecyclerView(getActivity());
        mCrimeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        //REFERENCE BELOW!
        NoteDBHelper DBH = new NoteDBHelper(getActivity());
        List<Message.Note> NoteList = DBH.getNoteList();
        mAdapter = new CrimeAdapter(NoteList);
        mCrimeRecyclerView.setAdapter(mAdapter);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.addRule(RelativeLayout.BELOW, parental.getId());
        mCrimeRecyclerView.setLayoutParams(params);

        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.structure_content_main, container, false);
        RelativeLayout frame = (RelativeLayout) view.findViewById(R.id.fragmentContainer);
        updateUI();
        frame.addView(parental);
        frame.addView(mCrimeRecyclerView);

        for (Fragment fr: getActivity().getSupportFragmentManager().getFragments()) {
            Log.e("TAG", "onStart: " + fr);
        }

        return view;
    }

    @Override
    public String setButtonName() {
        return getString(R.string.button_for_note_pager);
    }

    @Override
    public String setStringName() {
        return getString(R.string.hint_for_note_pager);
    }

    @Override
    public void SecondButtonOnClick(String string) {
        NoteDBHelper DBH = new NoteDBHelper(getActivity());
        SimpleDateFormat DF = new SimpleDateFormat("EEEE, MMMM dd, yyyy");
        DBH.addRec("Title", string, DF.format(new Date()));
        List<Message.Note> NoteList = DBH.getNoteList();
        mAdapter = new CrimeAdapter(NoteList);
        mCrimeRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void SilentiumSender(Message message) {
        NoteDBHelper DBH = new NoteDBHelper(getActivity());
        SimpleDateFormat DF = new SimpleDateFormat("EEEE, MMMM dd, yyyy");
        DBH.addRec("Title", message.toAnotherString(getContext()), DF.format(new Date()));
        FragmentManager manager = getActivity().getSupportFragmentManager();
        manager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        manager.beginTransaction().addToBackStack(null).replace(R.id.fragmentContainer, new NotePager()).commit();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    @Override
    public void onStart() {
        try {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.action_notes));
        } catch (Exception e) {
            e.fillInStackTrace();
        }
        super.onStart();
    }

    private void updateUI() {
        //REFERENCE ABOVE!
        NoteDBHelper DBH = new NoteDBHelper(getActivity());
        List<Message.Note> NoteList = DBH.getNoteList();
        if (mAdapter == null) {
            mAdapter = new CrimeAdapter(NoteList);
            mCrimeRecyclerView.setAdapter(mAdapter);
            //mCrimeRecyclerView.smoothScrollToPosition(0);
        } else {
            mAdapter.notifyItemChanged(TouchPosition);
            //mCrimeRecyclerView.smoothScrollToPosition(0);
        }
    }

    private class CrimeHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener, View.OnClickListener {

        private Message.Note mCrime;
        private TextView mTitleTextView;
        private TextView mNoteTextView;
        private TextView mDateTextView;

        public CrimeHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            mTitleTextView = (TextView) itemView.findViewById(R.id.note_title);
            mNoteTextView = (TextView) itemView.findViewById(R.id.note_text);
            mDateTextView = (TextView) itemView.findViewById(R.id.note_data);
        }
        public void bindCrime(Message.Note crime) {
            mCrime = crime;
            mTitleTextView.setText(mCrime.Title);
            mNoteTextView.setText(mCrime.Text);
            mDateTextView.setText(mCrime.CreateDate);
        }

        @Override
        public void onClick(View v) {

        }

        @Override
        public boolean onLongClick(View v) {
            VisualMeaning = mCrime.Text;
            if (!popup.isShowing()) {
                Log.e("TAG", "onClick: PopUP!! " + popup.toString());
                popup.setAnchorView(v);
                popup.setAdapter(adapter);
                popup.show();
            }
            return false;
        }
    }

    private class CrimeAdapter extends RecyclerView.Adapter<CrimeHolder> {

        private List<Message.Note> mCrimes;

        public CrimeAdapter(List<Message.Note> crimes) {
            mCrimes = crimes;
        }

        @Override
        public CrimeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.item_note, parent, false);
            return new CrimeHolder(view);
        }
        @Override
        public void onBindViewHolder(CrimeHolder holder, int position) {
            Message.Note crime = mCrimes.get(position);
            holder.bindCrime(crime);
        }
        @Override
        public int getItemCount() {
            return mCrimes.size();
        }
    }





    public void setDialog(final String meaning) {



        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService (Context.LAYOUT_INFLATER_SERVICE);
        View dialoglayout = inflater.inflate(R.layout.dialog_visualization_chiose, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder
                .setCancelable(true)
                .setView(dialoglayout);

        final FragmentManager fm = getActivity().getSupportFragmentManager();

        Button Vibro = (Button) dialoglayout.findViewById(R.id.vibration_button);
        Button Sound = (Button) dialoglayout.findViewById(R.id.sound_button);
        Button Flash = (Button) dialoglayout.findViewById(R.id.back_flash_button);

        Vibro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VisualizationDialog vzd = VisualizationDialog.newInstance(meaning, "vibro");
                altdlg.dismiss();
                vzd.show(fm, "lol");
            }
        });

        Sound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VisualizationDialog vzd = VisualizationDialog.newInstance(meaning, "sound");
                altdlg.dismiss();
                vzd.show(fm, "lol");
            }
        });

        Flash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VisualizationDialog vzd = VisualizationDialog.newInstance(meaning, "backFlash");
                altdlg.dismiss();
                vzd.show(fm, "lol");
            }
        });


        alertDialogBuilder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {

            }
        });

        altdlg = alertDialogBuilder.show();
    }
}