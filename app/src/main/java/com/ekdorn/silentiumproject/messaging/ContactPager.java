package com.ekdorn.silentiumproject.messaging;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ekdorn.silentiumproject.silent_core.Message;
import com.ekdorn.silentiumproject.R;
//import com.ekdorn.silentiumproject.silent_core.Sent;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * Created by User on 01.04.2017.
 */

public class ContactPager extends Fragment {

    public static final String ARG_CRIME_ID = "ru";
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("message");
    private RecyclerView mCrimeRecyclerView;
    private CrimeAdapter mAdapter;
    List<Message.Sent> SentList;
    FrameLayout piece;
    RelativeLayout framer;
    String child;
    Message msg;
    static Context context;

    public static ContactPager newInstance(String name, String type, String child, boolean isAdmin, String UrgentMessage) {
        Bundle args = new Bundle();
        args.putString("urgent", UrgentMessage);
        args.putString("lol", type);
        args.putSerializable("name", name);
        args.putSerializable(ARG_CRIME_ID, child);
        args.putSerializable("trt", isAdmin);
        ContactPager fragment = new ContactPager();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getContext();

        Log.e("TAG", "onClick: " + (String) getArguments().getSerializable("name"));

        msg = new Message();
        mCrimeRecyclerView = new RecyclerView(getActivity());
        mCrimeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        SentList = new ArrayList<>();

        child = getArguments().getString(ARG_CRIME_ID);

        mAdapter = new CrimeAdapter(SentList);
        mCrimeRecyclerView.setAdapter(mAdapter);

        setListener(child);

        piece = new FrameLayout(getActivity());
        ViewGroup.LayoutParams imageViewParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        piece.setLayoutParams(imageViewParams);
        piece.setId(R.id.fragmentDialog);

        ViewGroup.LayoutParams imageViewLayoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mCrimeRecyclerView.setLayoutParams(imageViewLayoutParams);

        framer = new RelativeLayout(getActivity());
        framer.setLayoutParams(imageViewLayoutParams);
        framer.addView(mCrimeRecyclerView);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.BELOW, piece.getId());
        framer.setLayoutParams(params);
    }

    @Override
    public void onStart() {
        try {
            ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle((String) getArguments().getSerializable("name"));
        } catch (Exception e) {
            e.fillInStackTrace();
        }
        super.onStart();
    }

    public void setListener(String destination){
        myRef.child(destination).child("messages").orderByChild("Date").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                HashMap<String, Object> value = (HashMap<String, Object>) dataSnapshot.getValue();

                SentList.add(0, new Message.Sent(value, context));

                mAdapter.notifyItemChanged(0);
                mCrimeRecyclerView.smoothScrollToPosition(0);

                Log.e(TAG, "onDataChange2: " + SentList.size());
                updateUI();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.w("TAG", "Failed to read value.", error.toException());
            }
        });
    }

    private void updateUI() {
        if (mAdapter == null) {
            mAdapter = new CrimeAdapter(SentList);
            mCrimeRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.notifyDataSetChanged();
        }
    }



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.structure_content_main, container, false);
        RelativeLayout frame = (RelativeLayout) view.findViewById(R.id.fragmentContainer);
        frame.addView(piece);
        frame.addView(framer);

        Log.e("TAG", "AAAAAAAAAAAAAAAAAAAAAAA: " + getArguments().getString("urgent"));

        if ((getArguments().getBoolean("trt"))||(!getArguments().getSerializable("name").equals("Silentium"))) {
            FragmentManager fm = getChildFragmentManager();
            try {
                fm.beginTransaction().replace(R.id.fragmentDialog, MessageSender.newInstance(child, (String) getArguments().getSerializable("name"), getArguments().getString("urgent"))).commit();
            } catch (Exception e) {
                e.printStackTrace();
                fm.beginTransaction().replace(R.id.fragmentDialog, MessageSender.newInstance(child, (String) getArguments().getSerializable("name"), "")).commit();
            }
        }
        return view;
    }

    private class CrimeHolder extends RecyclerView.ViewHolder /*implements View.OnClickListener*/ {

        private Message.Sent mCrime;
        private TextView mNoteTextView;
        private TextView mDateTextView;
        private TextView mAuthorTextView;

        public CrimeHolder(View itemView) {
            super(itemView);
            //itemView.setOnClickListener(this);
            mAuthorTextView = (TextView) itemView.findViewById(R.id.messageAuthor);
            mNoteTextView = (TextView) itemView.findViewById(R.id.message_text);
            mDateTextView = (TextView) itemView.findViewById(R.id.message_data);
        }
        public void bindCrime(Message.Sent crime) {
            mCrime = crime;
            mNoteTextView.setText(mCrime.Text);
            mDateTextView.setText(mCrime.Date);
            if (getArguments().getString("lol").equals(getString(R.string.chat_type_group))) {
                mAuthorTextView.setText(getArguments().getString("lol")/*mCrime.Author.substring(mCrime.Author.indexOf("@"))*/);
            } else if (getArguments().getString("lol").equals(getString(R.string.chat_type_common))) {
                mAuthorTextView.setText(getString(R.string.admin) + " " + mCrime.Author.substring(mCrime.Author.indexOf("&")));
            }
            if (!mCrime.Author.contains(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                params.addRule(RelativeLayout.ALIGN_PARENT_START);
                mNoteTextView.setLayoutParams(params);
                RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params1.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                params1.addRule(RelativeLayout.ALIGN_PARENT_START);
                params1.addRule(RelativeLayout.BELOW, mNoteTextView.getId());
                mAuthorTextView.setLayoutParams(params1);
                RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params2.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                params2.addRule(RelativeLayout.ALIGN_PARENT_START);
                params2.addRule(RelativeLayout.BELOW, mAuthorTextView.getId());
                mDateTextView.setLayoutParams(params2);

            }
            if (getArguments().getString("lol").equals(getString(R.string.chat_type_private))) {
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(0, 0);
                mAuthorTextView.setLayoutParams(params);
                RelativeLayout.LayoutParams params2 = (RelativeLayout.LayoutParams) mDateTextView.getLayoutParams();
                params2.addRule(RelativeLayout.BELOW, mNoteTextView.getId());
                mDateTextView.setLayoutParams(params2);
            }
        }
    }

    private class CrimeAdapter extends RecyclerView.Adapter<CrimeHolder> {

        private List<Message.Sent> mCrimes;

        public CrimeAdapter(List<Message.Sent> crimes) {
            mCrimes = crimes;
        }

        @Override
        public CrimeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.item_message, parent, false);
            return new CrimeHolder(view);
        }
        @Override
        public void onBindViewHolder(CrimeHolder holder, int position) {
            Message.Sent crime = mCrimes.get(position);
            holder.setIsRecyclable(false);
            holder.bindCrime(crime);

        }
        @Override
        public int getItemCount() {
            return mCrimes.size();
        }
    }
}
