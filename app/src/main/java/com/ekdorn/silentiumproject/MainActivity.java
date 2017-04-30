package com.ekdorn.silentiumproject;

import android.preference.PreferenceManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.ekdorn.silentiumproject.authentification.Authentification;
import com.ekdorn.silentiumproject.input.SilentiumButton;
import com.ekdorn.silentiumproject.messaging.ContactPager;
import com.ekdorn.silentiumproject.messaging.DialogPager;
import com.ekdorn.silentiumproject.notes.NotePager;
import com.ekdorn.silentiumproject.settings.Settings;
import com.ekdorn.silentiumproject.silent_core.Message;
import com.ekdorn.silentiumproject.silent_accessories.SingleDataRebaser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    //

    //https://habrahabr.ru/sandbox/34130/
    //http://stackoverflow.com/questions/18951495/is-there-something-like-a-vibrationpreference-similar-to-ringtonepreference

    static boolean flag = false;

    boolean isAdmin;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myUserRef;

    SilentiumButton frag1;
    NotePager frag2;
    DialogPager frag3;
    FragmentManager manager;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.structure_activity_main);
        if (getIntent().getStringExtra("DialogName") == null) {
            if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                Intent intent = new Intent(getApplicationContext(), Authentification.class);
                startActivityForResult(intent, 3);
            } else {
                StartActivity();
                manager = getSupportFragmentManager();
                manager.beginTransaction().replace(R.id.fragmentContainer, frag1).commit();
            }
        } else {
            flag = true;
            myUserRef = database.getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
            StartActivity();

            SharedPreferences shprf = this.getSharedPreferences(getString(R.string.silent_preferences), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = shprf.edit();
            editor.putInt("messageCount", 0);
            editor.apply();
            sedMessageRedirect();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        StartActivity();

        manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.fragmentContainer, frag1).commit();
    }

    public void StartActivity() {

        if (!this.getSharedPreferences(getString(R.string.silent_preferences), Context.MODE_PRIVATE).getBoolean("initialized", false)) {
            Log.e("TAG", "StartActivity: PREFERENCES NULL");
            SingleDataRebaser.transferToPreferences(getApplicationContext());
        }





        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        Map<String,?> keys = prefs.getAll();

        for(Map.Entry<String,?> entry : keys.entrySet()){
            Log.d("map values",entry.getKey() + ": " +
                    entry.getValue().toString());
        }


        Message msg = new Message("lol kek sas", getApplicationContext());
        Log.e("TAG", "StartActivity: " + msg.PatternCreator(msg.toString()));



        //User uui = User.get();
        //Log.e("TAG", "onClick: " + User.get().getUsers());



        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        frag1 = new SilentiumButton();
        frag2 = new NotePager();
        frag3 = new DialogPager();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);
        TextView iv = (TextView) header.findViewById(R.id.TV1);
        iv.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
        TextView tv = (TextView) header.findViewById(R.id.TV2);
        tv.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (flag) {
                finish();
            } else {
                FragmentManager fm = getSupportFragmentManager();
                try {
                    for (Fragment frag : fm.getFragments()) {
                        if (frag.isVisible()) {
                            FragmentManager childFm = frag.getChildFragmentManager();
                            if (childFm.getBackStackEntryCount() > 0) {
                                childFm.popBackStack();
                                return;
                            }
                        }
                    }
                    fm.popBackStack();
                } catch (Exception e) {
                    super.onBackPressed();
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_settings:
                Intent sintent = new Intent(getApplicationContext(), Settings.class);
                startActivityForResult(sintent, 2);
                return true;
            case R.id.menu_signout:
                FirebaseAuth.getInstance().signOut();

                Intent intent = new Intent(getApplicationContext(), Authentification.class);
                //TODO: cancel the device token.
                startActivityForResult(intent, 2);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        //fTrans = getFragmentManager().beginTransaction();
        manager = getSupportFragmentManager();


        switch (id) {
            case R.id.nav_camera:
                Log.e("TAG", "onNavigationItemSelected: Notes");
                if (!manager.getFragments().contains(frag2)) {
                    manager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    manager.beginTransaction().addToBackStack(null).replace(R.id.fragmentContainer, frag2).commit();
                }
                break;
            case R.id.nav_gallery:
                Log.e("TAG", "onNavigationItemSelected: Input");
                manager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                manager.beginTransaction().replace(R.id.fragmentContainer, frag1).commit();
                break;
            case R.id.nav_slideshow:
                Log.e("TAG", "onNavigationItemSelected: Chat");
                if (!manager.getFragments().contains(frag3)) {
                    manager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    manager.beginTransaction().addToBackStack(null).replace(R.id.fragmentContainer, frag3).commit();
                }
                break;
            case R.id.nav_manage:
                Log.e("TAG", "onNavigationItemSelected: Settings");
                Intent intent = new Intent(getApplicationContext(), Settings.class);
                startActivityForResult(intent, 2);
                break;
            case R.id.nav_share:
                break;
            case R.id.nav_send:
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void sedMessageRedirect() {
        myUserRef.child("isAdmin").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                FragmentManager afm = getSupportFragmentManager();
                FragmentTransaction ft = afm.beginTransaction();
                ft.addToBackStack(null);
                Log.e("TAG", "onDataChange: GOING TO THE DIALOGS");
                DialogPager.DisplayDialog dd = new DialogPager.DisplayDialog(getIntent().getStringExtra("DialogName"));
                ft.replace(R.id.fragmentContainer, ContactPager.newInstance(dd.DialogDisplayName,dd.DialogType, dd.DialogName, (boolean) dataSnapshot.getValue()));
                ft.commit();
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("TAG", "Failed to read value.", databaseError.toException());
            }
        });
    }
}