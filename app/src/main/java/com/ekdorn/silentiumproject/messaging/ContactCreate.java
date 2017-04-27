package com.ekdorn.silentiumproject.messaging;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ListPopupWindow;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ekdorn.silentiumproject.R;
import com.ekdorn.silentiumproject.silent_core.User;
import com.ekdorn.silentiumproject.silent_core.Message;
import com.ekdorn.silentiumproject.silent_core.SingleSilentiumOrInput;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

/**
 * Created by User on 09.04.2017.
 */

public class ContactCreate extends SingleSilentiumOrInput {
    RelativeLayout frame;
    RelativeLayout parental;
    ListView lv;
    Button btns;

    String Name;
    ListPopupWindow popup;
    PopUpAdaptor popUpAdapter;
    //ArrayList<String[]> popUpList;
    HashMap<String, User> popUpValueList;
    HashMap<String, User> resultUser;
    HashMap<String, User> totalList;

    boolean total;

    //static HashMap<String, User> finalUser;
    //static ArrayList<String> userNames;
    //static ArrayList<String> userUUID;
    UserListAdaptor adapter;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("users");

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Create a dialog...");

        //userNames = new ArrayList<>();
        //userUUID = new ArrayList<>();
        //popUpList = new ArrayList<>();
        popUpValueList = new HashMap<>();
        totalList = new HashMap<>();
        resultUser = new HashMap<>();
        //finalUser = new HashMap<>();


        total = true;

        popup = new ListPopupWindow(this.getActivity());
        popUpAdapter = new PopUpAdaptor(getActivity(), R.layout.item_pop_up, popUpValueList);
        popup.setAdapter(popUpAdapter);
        popup.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FBSearch(new ArrayList<>(popUpValueList.values()).get(position).Name);
            }
        });

        parental = CreateView();

        btns = new Button(getActivity());
        RelativeLayout.LayoutParams params4 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params4.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        btns.setLayoutParams(params4);
        btns.setText("Start dialog");
        btns.setId(R.id.endButton);

        lv = new ListView(getActivity());
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.addRule(RelativeLayout.BELOW, parental.getId());
        params.addRule(RelativeLayout.ABOVE, btns.getId());
        lv.setId(R.id.listView);
        lv.setLayoutParams(params);

        adapter = new UserListAdaptor(getActivity(), R.layout.item_pop_up, resultUser);

        lv.setAdapter(adapter);

        /*lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getContext(), (String) lv.getItemAtPosition(position), Toast.LENGTH_SHORT).show();
            }
        });*/

        GetUserNames();
    }

    public class PopUpAdaptor extends ArrayAdapter<User> {
        public PopUpAdaptor(Context context, int resource, HashMap<String, User> items) {
            super(context, resource, new ArrayList<>(items.values()));
        }
        @Override
        @NonNull
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                view = LayoutInflater.from(getContext()).inflate(R.layout.item_pop_up, null);
            }
            User p = getItem(position);
            Log.e("TAG", "getView: " + p.Name);
            Log.e("TAG", "getView: " + p.Email);
            TextView tt1 = (TextView) view.findViewById(R.id.popUpNameTextView);
            TextView tt2 = (TextView) view.findViewById(R.id.popUpEmailTextView);
            ImageView ii = (ImageView) view.findViewById(R.id.imageView4);
            ii.setVisibility(View.INVISIBLE);
            if (p.Email.contains("@silentium.notspec")) {
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(0, 0);
                tt2.setLayoutParams(params);
            }
            tt1.setText(p.Name);
            tt2.setText(p.Email);
            return view;
        }
    }

    public class UserListAdaptor extends ArrayAdapter<User> {
        public UserListAdaptor(Context context, int resource, HashMap<String, User> items) {
            super(context, resource, new ArrayList<>(items.values()));
        }
        @Override
        @NonNull
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                view = LayoutInflater.from(getContext()).inflate(R.layout.item_pop_up, null);
            }
            User p = getItem(position);
            final String userName = p.Name;
            Log.e("TAG", "getView: " + p.Name);
            Log.e("TAG", "getView: " + p.Email);
            TextView tt1 = (TextView) view.findViewById(R.id.popUpNameTextView);
            TextView tt2 = (TextView) view.findViewById(R.id.popUpEmailTextView);
            ImageView ii = (ImageView) view.findViewById(R.id.imageView4);
            ii.setVisibility(View.VISIBLE);
            ii.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ArrayList<String> als = new ArrayList<>(resultUser.keySet());
                    try {
                        for (int i = 0; i < resultUser.size(); i++) {
                            if (resultUser.get(als.get(i)).Name.equals(userName)) {
                                Log.e("TAG", "onClick: " + userName);
                                resultUser.remove(als.get(i));
                            }
                        }
                    } catch (Exception e) {
                        Log.e("TAG", "updatePopUpAdaptor: Removed or not");
                    }
                    adapter.notifyDataSetChanged();
                    adapter = new UserListAdaptor(getActivity(), R.layout.item_pop_up, resultUser);
                    lv.setAdapter(adapter);
                }
            });
            if (p.Email.contains("@silentium.notspec")) {
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(0, 0);
                tt2.setLayoutParams(params);
            }
            tt1.setText(p.Name);
            tt2.setText(p.Email);
            return view;
        }
    }

    public void GetName() {
        myRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Name = (String) dataSnapshot.getValue();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("TAG", "Failed to read value.", databaseError.toException());
            }
        });
    }

    public void GetUserNames(){
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap<String, Object> value = (HashMap<String, Object>) dataSnapshot.getValue();
                for (String str : value.keySet()) {
                    HashMap<String, Object> userDetails = (HashMap<String, Object>) value.get(str);
                    Log.e("TAG", "onDataChange: " + userDetails.get("Email").toString());
                    String [] user = new String [] {(String) userDetails.get("Name"), (String) userDetails.get("Email")};
                    totalList.put(str, new User(value.get(str)));
                    //popUpList.add(user);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("TAG", "Failed to read value.", databaseError.toException());
            }
        });
    }

    @Override
    public String setButtonName() {
        return "SEARCH";
    }

    @Override
    public String setStringName() {
        return "Enter the e-mail of your addressee";
    }

    public void FBSearch(final String string) {
        boolean b = false;
        Log.d("TAG", "doInBackground: " + string);
        for (String str : popUpValueList.keySet()) {
            Log.e("TAG", "onDataChange: " + popUpValueList.get(str).Name.toString());
            if (popUpValueList.get(str).Name.equals(string)) {
                b = true;
                resultUser.put(str, popUpValueList.get(str));
            }
        }

        adapter.notifyDataSetChanged();
        adapter = new UserListAdaptor(getActivity(), R.layout.item_pop_up, resultUser);
        lv.setAdapter(adapter);

        if (!b) {
            Toast.makeText(getActivity(), "Maybe you've typed incorrectly", Toast.LENGTH_SHORT).show();
        }

        if ((resultUser.size() >= 1) && (total)) {
            total = false;
        }
    }

    public void CreateDialog(HashMap<String, User> as, String str) {
        try {
            final String uuid = ((str.length() == 0) ? (UUID.randomUUID().toString() + FirebaseAuth.getInstance().getCurrentUser().getUid() + Name + new ArrayList<>(as.values()).get(0).Name) : (UUID.randomUUID().toString() + str));

            Log.e("TAG", "CreateDialog: " + str);
            Log.e("TAG", "CreateDialog: " + uuid);
            database.getReference("message").child(uuid).child("members").child(UUID.randomUUID().toString()).setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());
            for (String s : as.keySet()) {
                database.getReference("message").child(uuid).child("members").child(UUID.randomUUID().toString()).setValue(s);
            }
            database.getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Dialogs").child(UUID.randomUUID().toString()).setValue(uuid);
            for (String s : as.keySet()) {
                database.getReference("users").child(s).child("Dialogs").child(UUID.randomUUID().toString()).setValue(uuid);
            }
            FragmentManager afm = getActivity().getSupportFragmentManager();
            frame.removeAllViews();
            FragmentTransaction ft = afm.beginTransaction();
            ft.replace(R.id.fragmentContainer, new DialogPager());
            ft.commit();
        } catch (NullPointerException e) {
            Toast.makeText(getActivity(), "Specify the e-mail", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(getActivity(), "Something went terribly wrong", Toast.LENGTH_SHORT).show();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        GetName();
        View view = inflater.inflate(R.layout.structure_content_main, container, false);
        frame = (RelativeLayout) view.findViewById(R.id.fragmentContainer);
        frame.addView(parental);
        frame.addView(btns);
        frame.addView(lv);
        return view;
    }

    @Override
    public void SilentiumSender(Message message) {
        FBSearch(message.toAnotherString(getContext()));
    }

    @Override
    public void SecondButtonOnClick(String string) {
        Log.d("TAG", "doInBackground: " + string);
        ArrayList<String> EmailList = new ArrayList<>();
        for (User uui: totalList.values()) {
            Log.e("TAG", "onDataChAAAnge: " + uui.Email);
            EmailList.add(uui.Email);
        }
        if (string.equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())) {
            Toast.makeText(getActivity(), "You can't chat with yourself it's just silly", Toast.LENGTH_SHORT).show();
        } else if (EmailList.contains(string)) {
            Toast.makeText(getActivity(), "Other people can not chat with themselves too", Toast.LENGTH_SHORT).show();
        } else {
            FBSearch(string);
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btns.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (User uui: resultUser.values()) {
                    Log.e("TAG", "onDataChange: " + uui.toString());
                }
                CreateDialog(resultUser, GetTheSecondEditTextValue());
            }
        });
    }

    @Override
    public void TextChanged(EditText ed) {
        Log.e("TAG", "onClick: " + ed.getText());
        if (ed.getText().toString().length() >= 3) {
            updatePopUpAdaptor(ed.getText().toString().toLowerCase());

            if (!popup.isShowing()) {
                Log.e("TAG", "onClick: PopUP!! " + popup.toString());
                popup.setAnchorView(ed);
                popup.setInputMethodMode(ListPopupWindow.INPUT_METHOD_NEEDED);
                popup.show();
            }
            Log.e("TAG", "TextChanged: Shown " + popup.isShowing());
        } else {
            popup.dismiss();
            if (popUpValueList.size() != 0) {
                popUpValueList.clear();
            }
        }
    }

    public void updatePopUpAdaptor(String nameHint) {
        if (nameHint.length() == 3) {
            Log.e("TAG", "Data stored ");

            for (String name: totalList.keySet()) {
                Log.e("TAG", "onClick: " + name);
                if (totalList.get(name).Name.toLowerCase().contains(nameHint) || totalList.get(name).Email.toLowerCase().contains(nameHint)) {
                    popUpValueList.put(name, totalList.get(name));
                    Log.e("TAG", "onClick: VALUEADDED " + totalList.get(name).toString());
                }
            }
            Log.d("TAG", "updatePopUpAdaptor: " + popUpValueList.toString());
            popUpAdapter.notifyDataSetChanged();
            popUpAdapter = new PopUpAdaptor(getActivity(), R.layout.item_pop_up, popUpValueList);
            popup.setAdapter(popUpAdapter);
            Log.d("TAG", "updatePopUpAdaptor: " + popUpAdapter.getCount());
        } else {
            Log.e("TAG", "Updated ");
            ArrayList<String> als = new ArrayList<>(popUpValueList.keySet());
            try {
                for (int i = 0; i < popUpValueList.size(); i++) {
                    if (!(popUpValueList.get(als.get(i)).Name.toLowerCase().contains(nameHint)) || (popUpValueList.get(als.get(i)).Email.toLowerCase().contains(nameHint))) {
                        Log.e("TAG", "onClick: " + nameHint);
                        popUpValueList.remove(als.get(i));
                    }
                }
            } catch (Exception e) {
                Log.e("TAG", "updatePopUpAdaptor: Removed or not");
            }
            popUpAdapter.notifyDataSetChanged();
            popUpAdapter = new PopUpAdaptor(getActivity(), R.layout.item_pop_up, popUpValueList);
            popup.setAdapter(popUpAdapter);
        }
    }
}