package com.example.ainurbayanova.attendancesystem;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ainurbayanova.attendancesystem.activities.AddUser;
import com.example.ainurbayanova.attendancesystem.activities.Add_News;
import com.example.ainurbayanova.attendancesystem.activities.LoginPage;
import com.example.ainurbayanova.attendancesystem.activities.Profile;
import com.example.ainurbayanova.attendancesystem.activities.aboutUsActivity;
import com.example.ainurbayanova.attendancesystem.activities.calendarActivity;
import com.example.ainurbayanova.attendancesystem.fragments.Attendance_fragment;
import com.example.ainurbayanova.attendancesystem.fragments.News_Fragments;
import com.example.ainurbayanova.attendancesystem.fragments.NoteFragment;
import com.example.ainurbayanova.attendancesystem.fragments.ScheduleFragment;
import com.example.ainurbayanova.attendancesystem.fragments.User_fragment;
import com.example.ainurbayanova.attendancesystem.interfaces.ShowInterface;
import com.example.ainurbayanova.attendancesystem.modules.News;
import com.example.ainurbayanova.attendancesystem.modules.User;
import com.example.ainurbayanova.attendancesystem.services.ParentServices;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.miguelcatalan.materialsearchview.MaterialSearchView;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    MaterialSearchView searchView;
    FirebaseAuth auth;
    FloatingActionButton fab;
    FloatingActionButton fab2;
    FirebaseUser user;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    User_fragment user_fragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        user_fragment = new User_fragment();
        user = FirebaseAuth.getInstance().getCurrentUser();
        fab = findViewById(R.id.fabBtn);
        fab2 = findViewById(R.id.fabBtn2);
        if(user != null){
            String email = user.getEmail();
            if(!email.equals("admin@sdcl.kz")){
                fab.setVisibility(View.GONE);
                fab2.setVisibility(View.GONE);
            }
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddUser.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivityForResult(intent,1);
            }
        });
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Add_News.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        });

        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        @SuppressLint("ResourceType") ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.drawable.ic_menu_black_24dp, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        getSupportFragmentManager().beginTransaction().replace(R.id.content, new Attendance_fragment()).commit();
        navigationView.setCheckedItem(R.id.nav_attendance);

        View header = navigationView.inflateHeaderView(R.layout.nav_header_main);
        LinearLayout linearLayout = header.findViewById(R.id.nav_header);
        TextView textView = header.findViewById(R.id.textView);
        textView.setText(user.getEmail());

        auth = FirebaseAuth.getInstance();
        changeFragment(new Attendance_fragment());
//        checkInet();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(user.getEmail().equals("parent@sdcl.kz")){
            startService(new Intent(this, ParentServices.class));
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("info","We are destroyed");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
        Log.i("info","We are destroyed");
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.attendance_menu,menu);
        MenuItem item = menu.findItem(R.id.calendar);
        MenuItem profile = menu.findItem(R.id.profile);
        profile.setVisible(false);
        if(!user.getEmail().equals("admin@sdcl.kz"))
            item.setVisible(false);

//        getMenuInflater().inflate(R.menu.main, menu);

        /*searchView = findViewById(R.id.search_view);
        searchView.closeSearch();

        MenuItem item = menu.findItem(R.id.search);
        searchView.setMenuItem(item);
        searchView.setHint("Search");
        searchView.setHintTextColor(getResources().getColor(R.color.grays));
        searchView.setVoiceSearch(true);
        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                searchView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onSearchViewClosed() {

            }
        });
        */
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.sign_out:

                new AlertDialog.Builder(MainActivity.this, R.style.AlertDialogTheme)
                        .setTitle("Выход")
                        .setMessage("Выход из " + user.getEmail() + "?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @SuppressLint("MissingPermission")
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                auth.signOut();
                                startActivity(new Intent(MainActivity.this, LoginPage.class));
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .show();
                break;
            case R.id.calendar:
                startActivity(new Intent(this,calendarActivity.class));
                break;
            case R.id.profile:
                break;
        }

        //noinspection SimplifiableIfStatement


        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("RestrictedApi")
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_users) {
            changeFragment(user_fragment);
            if(user.getEmail().equals("admin@sdcl.kz")){
                fab.setVisibility(View.VISIBLE);
                fab2.setVisibility(View.GONE);
            }
            if(user.getEmail().equals("parent@sdcl.kz")){
                databaseReference.child("user_list").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User item = null;

                        for (DataSnapshot data:dataSnapshot.getChildren()){
                            if(data.getKey().equals("i1560311579343")){
                                item = data.getValue(User.class);
                            }
                        }
                        Intent intent = new Intent(MainActivity.this, Profile.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("user", item);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
            getSupportActionBar().setTitle("Users");
        } else if (id == R.id.nav_attendance) {
            changeFragment(new Attendance_fragment());
            if(user.getEmail().equals("admin@sdcl.kz")) {
                fab.setVisibility(View.GONE);
                fab2.setVisibility(View.GONE);
            }
            getSupportActionBar().setTitle("Attendance");
        }
        else if(id == R.id.news){
            changeFragment(new News_Fragments());
            if(user.getEmail().equals("admin@sdcl.kz")){
                fab.setVisibility(View.GONE);
                fab2.setVisibility(View.VISIBLE);
            }
            getSupportActionBar().setTitle("News");

        }

        else if(id == R.id.group){
            startActivity(new Intent(this,aboutUsActivity.class));
        }
        else if(id == R.id.schedule){
            changeFragment(new ScheduleFragment());
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {

                changeFragment(user_fragment);
            }
        }
    }

    private void changeFragment(Fragment targetFragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content, targetFragment, "fragment")
                .commit();
    }
}
