package com.example.ainurbayanova.attendancesystem.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.ainurbayanova.attendancesystem.R;
import com.example.ainurbayanova.attendancesystem.database.StoreDatabase;
import com.example.ainurbayanova.attendancesystem.fragments.profile_fragments.ProfileAttendanceFragment;
import com.example.ainurbayanova.attendancesystem.fragments.profile_fragments.ProfileLateDays;
import com.example.ainurbayanova.attendancesystem.fragments.profile_fragments.ProfileSkipFragment;
import com.example.ainurbayanova.attendancesystem.modules.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class Profile extends AppCompatActivity {
    Toolbar toolbar;
    AppBarLayout appBarLayout;
    StoreDatabase db;
    TextView username, phoneNumber;
    TextView totalLate, totalEarly;
    CircleImageView userImage;
    int USER_EDIT = 97;
    StoreDatabase storeDb;
    SQLiteDatabase sqdb;
    public User user;
    Bundle bundle;
    StorageReference storageReference;
    DatabaseReference mDatabase;
    final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 77;
    LayoutAnimationController animation;
    String monthList[];
    ViewPager viewPager;
    TabLayout tabLayout;
    ProfileAttendanceFragment profileAttendanceFragment;
    ProfileSkipFragment profileSkipFragment;
    ProfileLateDays profileLateDays;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        initWidgets();
        initializeToolbar();

        bundle = getIntent().getExtras();
        user = (User) bundle.getSerializable("user");

        storageReference = FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        db = new StoreDatabase(this);

        initializeBundle(bundle, user);

    }


    public void initWidgets() {
        toolbar = findViewById(R.id.toolbars);
        appBarLayout = findViewById(R.id.app_bar);
        profileAttendanceFragment = new ProfileAttendanceFragment();
        profileSkipFragment = new ProfileSkipFragment();
        profileLateDays = new ProfileLateDays();

        userImage = findViewById(R.id.userImage);
        username = findViewById(R.id.userName);
        phoneNumber = findViewById(R.id.phoneNumber);
        totalLate = findViewById(R.id.totalLate);
        totalEarly = findViewById(R.id.totalEarly);
        monthList = getResources().getStringArray(R.array.monthsListInEnglish);

        storeDb = new StoreDatabase(this);
        sqdb = storeDb.getWritableDatabase();

//        adapter = new ProfileAttendanceAdapter(this, attendance_list);
//        attendanceRecyclerView.setAdapter(adapter);

        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);

        animation = AnimationUtils.loadLayoutAnimation(this, R.anim.layout_animation_fall_down);

        setupViewPager(viewPager);
        manageDate();
    }

    public void initializeToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Report for " + monthName);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    public void initializeBundle(Bundle bundle, User u) {
        if (bundle != null && u != null) {
            if(!u.getPhoto().equals("no photo")){
                Glide.with(this)
                        .load(u.getPhoto())
                        .into(userImage);
            }

            username.setText(u.getInfo());
            phoneNumber.setText(u.getPhoneNumber());
            totalLate.setText(u.getCardNumber());
        }
    }

    DateFormat dateF;
    String date, curMonthNumber, monthName;

    public void manageDate() {
        dateF = new SimpleDateFormat("dd_MM_yy");//01.12.19
        date = dateF.format(Calendar.getInstance().getTime());
        curMonthNumber = date.split("_")[1];

        monthName = monthList[Integer.parseInt(date.split("_")[1]) - 1];
    }

    private void setupViewPager(ViewPager viewPager) {
        SimplePageFragmentAdapter adapter = new SimplePageFragmentAdapter(getSupportFragmentManager());
        Bundle bundle = getIntent().getExtras();
        adapter.addFragment(profileAttendanceFragment, "Participants");
        adapter.addFragment(profileSkipFragment, "Missed days");
        adapter.addFragment(profileLateDays, "Late days");
        profileSkipFragment.setArguments(bundle);
        profileAttendanceFragment.setArguments(bundle);
        profileLateDays.setArguments(bundle);
//        adapter.addFragment(new ReadedBookListFragment(), "Readed");
//        adapter.addFragment(new ReviewsForBookFragment(), "Reviews");
//        adapter.addFragment(new RecommendationBookListFragment(), "Recommendations");

        viewPager.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.detail_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.edit:

                Intent intent = new Intent(this, EditUser.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("user", user);
                intent.putExtras(bundle);
                startActivity(intent);

                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == USER_EDIT) {
            if (resultCode == Activity.RESULT_OK) {

                Bundle bundle = data.getExtras();
                User us = (User) bundle.getSerializable("edited_user");
                initializeBundle(bundle, us);

            }
            if (resultCode == Activity.RESULT_CANCELED) {
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CALL_PHONE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {

                }
                return;
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public class SimplePageFragmentAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private List<String> titles = new ArrayList<>();

        public SimplePageFragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            return mFragmentList.get(i);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String one) {
            mFragmentList.add(fragment);
            titles.add(one);
        }
    }

}