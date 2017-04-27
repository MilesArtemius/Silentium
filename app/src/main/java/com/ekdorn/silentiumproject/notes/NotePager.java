package com.ekdorn.silentiumproject.notes;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
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
import com.ekdorn.silentiumproject.silent_core.Message;
import com.ekdorn.silentiumproject.R;

import java.util.ArrayList;
import java.util.List;

public class NotePager extends Fragment {

    AlertDialog altdlg;

    private RecyclerView mCrimeRecyclerView;
    private CrimeAdapter mAdapter;
    private int TouchPosition;

    ListPopupWindow popup;
    ListAdapter adapter;
    ArrayList<String> popUpList;

    static String VisualMeaning;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Silent Notes");

        popUpList = new ArrayList<>();
        popUpList.add("Visualise");
        popup = new ListPopupWindow(getContext());
        popup.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String s = popUpList.get(position);
                switch (s) {
                    case "Visualise":
                        setDialog(VisualMeaning);
                        break;
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
        ViewGroup.LayoutParams imageViewLayoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mCrimeRecyclerView.setLayoutParams(imageViewLayoutParams);
    }

    /*public static Rect locateView(View v) {
        int[] loc_int = new int[2];
        if (v == null) return null;
        try {
            v.getLocationOnScreen(loc_int);
        } catch (NullPointerException npe) {
            //Happens when the view doesn't exist on screen anymore.
            return null;
        }
        Rect location = new Rect();
        location.left = loc_int[0];
        location.top = loc_int[1];
        location.right = location.left + v.getWidth();
        location.bottom = location.top + v.getHeight();
        return location;
    }*/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.structure_content_main, container, false);
        RelativeLayout frame = (RelativeLayout) view.findViewById(R.id.fragmentContainer);
        updateUI();
        frame.addView(mCrimeRecyclerView);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    private void updateUI() {
        //REFERENCE ABOVE!
        NoteDBHelper DBH = new NoteDBHelper(getActivity());
        List<Message.Note> NoteList = DBH.getNoteList();
        if (mAdapter == null) {
            mAdapter = new CrimeAdapter(NoteList);
            mCrimeRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.notifyItemChanged(TouchPosition);
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
        Button Light = (Button) dialoglayout.findViewById(R.id.front_flash_button);
        Button Flash = (Button) dialoglayout.findViewById(R.id.back_flash_button);

        Vibro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VisualizationDialog vzd = VisualizationDialog.newInstance(meaning, "vibro");
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