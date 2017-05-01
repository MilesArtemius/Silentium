package com.ekdorn.silentiumproject.messaging;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ekdorn.silentiumproject.R;
import com.ekdorn.silentiumproject.authentification.SignNewUserIn;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.ekdorn.silentiumproject.messaging.ContactPager.ARG_CRIME_ID;

/**
 * Created by User on 02.04.2017.
 */

public class DialogPager extends Fragment {
    private ProgressBar pb;
    private Handler mHandler;

    private RecyclerView mCrimeRecyclerView;
    private CrimeAdapter mAdapter;
    List<DisplayDialog> TalkList;
    RelativeLayout frame;
    boolean isAdmin;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myUserRef = database.getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
    //User.UserUI CurrentUser;

    public static DialogPager newInstance(String UrgentMessage) {
        Bundle args = new Bundle();
        args.putSerializable("urgent", UrgentMessage);
        DialogPager fragment = new DialogPager();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Silent chats");

        //CurrentUser = new User.UserUI();

        pb = new ProgressBar(getContext());
        mHandler = new Handler();

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        params.addRule(RelativeLayout.CENTER_VERTICAL);
        pb.setLayoutParams(params);

        mCrimeRecyclerView = new RecyclerView(getActivity());
        mCrimeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        TalkList = new ArrayList<>();

        //setUserListener();

        //getAdminInstance();

        mAdapter = new CrimeAdapter(TalkList);
        mCrimeRecyclerView.setAdapter(mAdapter);

        Log.e("TAG", "onChildAdded: " + FirebaseAuth.getInstance().getCurrentUser().getUid());
        //Log.e("TAG", "onChildAdded: " + CurrentUser.toString());

        ViewGroup.LayoutParams imageViewLayoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mCrimeRecyclerView.setLayoutParams(imageViewLayoutParams);
    }

    @Override
    public void onStart() {
        super.onStart();

        TalkList.clear();
        setUserListener();
        getAdminInstance();
    }

    private void updateUI() {
        if (mAdapter == null) {
            mAdapter = new CrimeAdapter(TalkList);
            mCrimeRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.notifyDataSetChanged();
            Log.e("LALALALALAL", "NOTIFYING");
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.findItem(R.id.menu_item_new_crime).setVisible(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_new_crime:
                FragmentManager afm = getActivity().getSupportFragmentManager();
                frame.removeAllViews();
                FragmentTransaction ft = afm.beginTransaction();
                ft.addToBackStack(null);
                ft.replace(R.id.fragmentContainer, new ContactCreate());
                ft.commit();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void getAdminInstance() {
        myUserRef.child("isAdmin").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                isAdmin = (boolean) dataSnapshot.getValue();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("TAG", "Failed to read value.", databaseError.toException());
            }
        });
    }

    public void setUserListener() {
        myUserRef.child("Dialogs").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                int progress = 0;

                HashMap<String, String> Dialogs = (HashMap<String, String>) dataSnapshot.getValue();

                Log.e("TAG", "onChildAdded: " + Dialogs);

                for (String s: Dialogs.values()) {
                    DisplayDialog dd = new DisplayDialog(s);
                    if (!TalkList.contains(dd)) {
                        Log.e("TAG", "onDataChange: " + dd.toString());
                        TalkList.add(dd);
                        progress += (Dialogs.size()/100);
                    }
                    pb.setProgress(progress);
                }

                mAdapter = new CrimeAdapter(TalkList);
                mCrimeRecyclerView.setAdapter(mAdapter);

                if (pb.getParent() != null) {
                    frame.removeView(pb);
                    Log.e("TAG", "onDataChange: list");
                    frame.addView(mCrimeRecyclerView);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("TAG", "Failed to read value.", databaseError.toException());
            }
        });
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.structure_content_main, container, false);
        frame = (RelativeLayout) view.findViewById(R.id.fragmentContainer);
        Log.e("TAG", "onDataChange: progress added");
        frame.addView(pb);
        return view;
    }

    private class CrimeHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private DisplayDialog mCrime;
        private TextView mNoteTextView;
        private TextView mDateTextView;
        //private ImageView mImageIcon;

        public CrimeHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mDateTextView = (TextView) itemView.findViewById(R.id.dialog_title);
            mNoteTextView = (TextView) itemView.findViewById(R.id.dialog_text);
            //mImageIcon = (ImageView) itemView.findViewById(R.id.imageView3);
        }
        public void bindCrime(DisplayDialog crime) {
            mCrime = crime;
            mDateTextView.setText(mCrime.DialogDisplayName);
            mNoteTextView.setText(mCrime.DialogType);
            //mImageIcon.setImageBitmap();
        }

        @Override
        public void onClick(View v) {
            FragmentManager afm = getActivity().getSupportFragmentManager();
            frame.removeAllViews();
            FragmentTransaction ft = afm.beginTransaction();
            Log.e("TAG", "onClick: " + mCrime.DialogDisplayName);
            ft.addToBackStack(null);
            try {
                Log.e("TAG", "FinalMessage: " + getArguments().getString("urgent"));
                ft.replace(R.id.fragmentContainer, ContactPager.newInstance(mCrime.DialogDisplayName, mCrime.DialogType, mCrime.DialogName, isAdmin, getArguments().getString("urgent")));
                getArguments().remove("urgent");
            } catch (Exception e) {
                ft.replace(R.id.fragmentContainer, ContactPager.newInstance(mCrime.DialogDisplayName, mCrime.DialogType, mCrime.DialogName, isAdmin, ""));
                e.printStackTrace();
            }
            ft.commit();
        }
    }

    private class CrimeAdapter extends RecyclerView.Adapter<CrimeHolder> {

        private List<DisplayDialog> mCrimes;

        public CrimeAdapter(List<DisplayDialog> crimes) {
            mCrimes = crimes;
        }

        @Override
        public CrimeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.item_dialog, parent, false);
            return new CrimeHolder(view);
        }
        @Override
        public void onBindViewHolder(CrimeHolder holder, int position) {
            DisplayDialog crime = mCrimes.get(position);
            holder.setIsRecyclable(false);
            holder.bindCrime(crime);

        }
        @Override
        public int getItemCount() {
            return mCrimes.size();
        }
    }

    public static class DisplayDialog {
        public String DialogName;
        public String DialogDisplayName;
        public String DialogType;
        public DisplayDialog(final String dialogName) {
            DialogName = dialogName;
            if (dialogName.equals("Silentium")) {
                DialogDisplayName = "Silentium";
                DialogType = "Common broadcast";
            } else if (dialogName.contains(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                if (dialogName.substring(36, 64).equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                    DialogDisplayName = dialogName.substring(dialogName.lastIndexOf("&"));
                } else {
                    DialogDisplayName = dialogName.substring(dialogName.indexOf("&"), dialogName.lastIndexOf("&"));
                }

                DialogType = "Private chat";
                Log.e("TAG", "onDataChange: " + dialogName.substring(36, 64));
                Log.e("TAG", "onDataChange: " + FirebaseAuth.getInstance().getCurrentUser().getUid());
                Log.e("TAG", "onDataChange: " + dialogName.substring(36, 64).equals(FirebaseAuth.getInstance().getCurrentUser().getUid()));
                Log.e("TAG", "onDataChange: " + dialogName.substring(36, 64).contains(FirebaseAuth.getInstance().getCurrentUser().getUid()));
                Log.e("TAG", "onDataChange: " + FirebaseAuth.getInstance().getCurrentUser().getUid().contains(dialogName.substring(36, 64)));
                Log.e("TAG", "onDataChange: " + dialogName);
                Log.e("TAG", "onDataChange: " + DialogDisplayName);
                Log.e("TAG", "onDataChange: " + DialogType);
            } else {
                DialogDisplayName = dialogName.substring(36);
                DialogType = "Group chat";
            }
        }

        /*public Bitmap getIcon() {
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();

            StorageReference islandRef;
            islandRef = storageRef.child("user_profile/Default.png");

            File localFile = null;
            try {
                localFile = File.createTempFile("picture", ".png");
            } catch (IOException e) {
                e.printStackTrace();
            }

            final File finalLocalFile = localFile;
            islandRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    String filePath = finalLocalFile.getPath();
                    Log.e("TAG", "onSuccess: " + taskSnapshot);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                }
            });

            String filePath = localFile.getPath();

            Log.e("TAG", "getIcon: " + filePath);
            Log.e("TAG", "getIcon: " + localFile);

            return BitmapFactory.decodeFile(filePath);
        }*/
    }
}
