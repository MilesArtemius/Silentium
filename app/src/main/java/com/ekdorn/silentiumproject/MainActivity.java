package com.ekdorn.silentiumproject;

import android.content.DialogInterface;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.ekdorn.silentiumproject.authentication.Authentication;
import com.ekdorn.silentiumproject.authentication.FireBaser;
import com.ekdorn.silentiumproject.input.SilentiumButton;
import com.ekdorn.silentiumproject.messaging.DialogPager;
import com.ekdorn.silentiumproject.messaging.MessageSender;
import com.ekdorn.silentiumproject.notes.NotePager;
import com.ekdorn.silentiumproject.settings.Settings;
import com.ekdorn.silentiumproject.silent_core.SingleDataRebaser;
import com.ekdorn.silentiumproject.silent_statics.Prefs;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.RemoteMessage;

import java.lang.ref.WeakReference;
import java.util.Map;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    //https://habrahabr.ru/sandbox/34130/
    //http://stackoverflow.com/questions/18951495/is-there-something-like-a-vibrationpreference-similar-to-ringtonepreference

    SilentiumButton component_SilentiumButton;
    NotePager component_NotePager;
    DialogPager component_DialogPager;
    FragmentManager manager;

    public static WeakReference<MainActivity> activity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.structure_activity_main);

        activity = new WeakReference<>(this);

        //if (getIntent().getStringExtra("DialogName") == null) {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Intent intent = new Intent(getApplicationContext(), Authentication.class);
            startActivityForResult(intent, 3);
        } else {
            StartActivity();
        }
        /*} else {
            flag = true;
            myUserRef = database.getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
            StartActivity();

            SharedPreferences shprf = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = shprf.edit();
            editor.putInt("messageCount", 0);
            editor.apply();

            sedMessageRedirect();
        }*/
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("TAG", "onActivityResult: AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        if (resultCode == RESULT_OK) {
            StartActivity();
        } else {
            Toast.makeText(this, "Other device cancelled reauthentification", Toast.LENGTH_SHORT).show();
            FireBaser.signOut(new FireBaser.OnVoidResult() {
                @Override
                public void onResult() {
                    finish();
                }
            });
        }
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











        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        component_SilentiumButton = new SilentiumButton();
        component_NotePager = new NotePager();
        component_DialogPager = new DialogPager();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);
        TextView iv = (TextView) header.findViewById(R.id.TV1);
        iv.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
        TextView tv = (TextView) header.findViewById(R.id.TV2);
        if (!FirebaseAuth.getInstance().getCurrentUser().getEmail().contains("@silentium.notspec")) {
            tv.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        } else {
            tv.setText(getString(R.string.not_specified_email));
        }

        manager = this.getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.fragmentContainer, component_SilentiumButton).commit();
    }





    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            manager.beginTransaction().replace(R.id.fragmentContainer, component_DialogPager).commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
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
                FireBaser.signOut(new FireBaser.OnVoidResult() {
                    @Override
                    public void onResult() {
                        FirebaseAuth.getInstance().signOut();
                        Intent intent = new Intent(getApplicationContext(), Authentication.class);
                        startActivityForResult(intent, 2);
                    }
                });
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        manager = getSupportFragmentManager();

        switch (item.getItemId()) {
            case R.id.nav_notes:
                Log.e("TAG", "onNavigationItemSelected: Notes");
                manager.beginTransaction().replace(R.id.fragmentContainer, component_NotePager).commit();
                break;
            case R.id.nav_input:
                Log.e("TAG", "onNavigationItemSelected: Input");
                manager.beginTransaction().replace(R.id.fragmentContainer, component_SilentiumButton).commit();
                break;
            case R.id.nav_messaging:
                Log.e("TAG", "onNavigationItemSelected: Chat");
                manager.beginTransaction().replace(R.id.fragmentContainer, component_DialogPager).commit();
                break;
            case R.id.nav_settings:
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

    /*private void sedMessageRedirect() {
        myUserRef.child("isAdmin").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                FragmentManager afm = getSupportFragmentManager();
                FragmentTransaction ft = afm.beginTransaction();
                ft.addToBackStack(null);
                Log.e("TAG", "onDataChange: GOING TO THE DIALOGS");
                DialogPager.DisplayDialog dd = new DialogPager.DisplayDialog(getIntent().getStringExtra("DialogName"), getApplicationContext());
                ft.replace(R.id.fragmentContainer, ContactPager.newInstance(dd.DialogDisplayName,dd.DialogType, dd.DialogName, (boolean) dataSnapshot.getValue(), ""));
                ft.commit();
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("TAG", "Failed to read value.", databaseError.toException());
            }
        });
    }*/

    public void showDialog(final RemoteMessage remoteMessage) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder tb = new AlertDialog.Builder(MainActivity.this);
                tb.setTitle("Some app logged in!");
                tb.setMessage(remoteMessage.getData().get("body"));
                tb.setPositiveButton("send", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MessageSender.sendSpecial(MessageSender.PRIVATE_KEY_RESPONSE, "allow", Prefs.getUser(getApplicationContext(), Prefs.PRIVATE_KEY));
                    }
                });
                tb.setNegativeButton("deny", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MessageSender.sendSpecial(MessageSender.PRIVATE_KEY_RESPONSE, "deny", remoteMessage.getData().get("body"));
                    }
                });

                tb.show();
            }
        });
    }
}