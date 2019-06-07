package com.example.ainurbayanova.attendancesystem.fragments;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.ainurbayanova.attendancesystem.R;
import com.example.ainurbayanova.attendancesystem.activities.AttendanceActivity;
import com.example.ainurbayanova.attendancesystem.activities.LoginPage;
import com.example.ainurbayanova.attendancesystem.adapter.AttendanceAdapter;
import com.example.ainurbayanova.attendancesystem.database.StoreDatabase;
import com.example.ainurbayanova.attendancesystem.modules.Attendance;
import com.example.ainurbayanova.attendancesystem.modules.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;

import static com.example.ainurbayanova.attendancesystem.database.StoreDatabase.COLUMN_CARD_NUMBER;
import static com.example.ainurbayanova.attendancesystem.database.StoreDatabase.COLUMN_FKEY;
import static com.example.ainurbayanova.attendancesystem.database.StoreDatabase.COLUMN_F_PHOTO_NAME;
import static com.example.ainurbayanova.attendancesystem.database.StoreDatabase.COLUMN_INFO;
import static com.example.ainurbayanova.attendancesystem.database.StoreDatabase.COLUMN_PHONE;
import static com.example.ainurbayanova.attendancesystem.database.StoreDatabase.COLUMN_PHOTO;
import static com.example.ainurbayanova.attendancesystem.database.StoreDatabase.TABLE_USER;

public class Attendance_fragment extends Fragment {

    RecyclerView recyclerView;
    AttendanceAdapter adapter;
    HashSet<Attendance> attendance_list;
    ArrayList<Attendance> attendances = new ArrayList<>();
    HashSet<Attendance> todayList;
    HashSet<Attendance> pastPastDayList;
    HashSet<Attendance> pastDayList;
    TextView pastPastDay;
    TextView pastDay;
    TextView nowDay;
    SQLiteDatabase sqdb;
    StoreDatabase storeDb;
    DatabaseReference mDatabaseRef;
    ArrayList<String> dateList;
    FirebaseAuth auth;
    View view;
    LayoutAnimationController animation;
    Calendar calendar;
    String[] dayNames;
    TextView text;
    TextView noLayout;
    FirebaseUser user;
    ProgressBar progressBar;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_attendance, container, false);
        user = FirebaseAuth.getInstance().getCurrentUser();
        initWidgets();
        return view;
    }

    public void initWidgets() {
        recyclerView = view.findViewById(R.id.recyclerView);
        nowDay = view.findViewById(R.id.nowDay);
        text = view.findViewById(R.id.text);
        noLayout = view.findViewById(R.id.noLayout);
        progressBar = view.findViewById(R.id.progressBar);

        attendance_list = new HashSet<>();
        todayList = new HashSet<>();
        pastPastDayList = new HashSet<>();
        pastDayList = new HashSet<>();

        mDatabaseRef = FirebaseDatabase.getInstance().getReference();
        storeDb = new StoreDatabase(getActivity());
        sqdb = storeDb.getWritableDatabase();
        dateList = new ArrayList<>();
        auth = FirebaseAuth.getInstance();

//        adapter = new AttendanceAdapter(getActivity(), attendance_list, dayNames);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        int resId = R.anim.layout_animation_fall_down;
        animation = AnimationUtils.loadLayoutAnimation(getActivity(), resId);

        calendar = Calendar.getInstance();
        dayNames = getResources().getStringArray(R.array.dayNames);

        checkInternetConnection();
        manageDate();

        getAttendanceUserFromFr();
        setDatesForMenuButtons();

        checkVersion();

    }

    public void setDatesForMenuButtons() {
        String dateName;

        dateName = getFullDayName(today);
        nowDay.setText("Сегодня: "+dateName+", "+today.replace("_", "."));

    }

    public void getAttendanceUserFromFr() {
        mDatabaseRef.child("attendance").child(today).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    if(data.child("closed").exists()){
                        Attendance attendance = new Attendance(today,data.getKey(),data.child("entered").child("time").getValue().toString(), data.child("closed").child("time").getValue().toString());
                        attendance_list.add(attendance);
                    }
                    else{
                        Attendance attendance = new Attendance(today,data.getKey(),data.child("entered").child("time").getValue().toString(), "no");
                        attendance_list.add(attendance);
                    }
                }
                mDatabaseRef.child("user_list").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot data : dataSnapshot.getChildren()) {
                            for (Attendance attendance:attendance_list) {
                                if(user.getEmail().equals("teacher@sdcl.kz")){
                                    if (data.getKey().equals(attendance.getfKey()) && data.child("spinner").getValue().toString().equals("3-02")) {
                                        Attendance new_attend = new Attendance(attendance.getDate(),
                                                attendance.getfKey(),
                                                data.child("info").getValue().toString(),
                                                data.child("photo").getValue().toString(),
                                                attendance.getEnterDate(),
                                                attendance.getExitDate());
                                        attendances.add(new_attend);
                                    }
                                }
                                if(user.getEmail().equals("parent@sdcl.kz")){
                                    if (data.getKey().equals(attendance.getfKey())) {
                                        if(attendance.getfKey().equals("i1559550628490")){
                                            Attendance new_attend = new Attendance(attendance.getDate(),
                                                    attendance.getfKey(),
                                                    data.child("info").getValue().toString(),
                                                    data.child("photo").getValue().toString(),
                                                    attendance.getEnterDate(),
                                                    attendance.getExitDate());
                                            attendances.add(new_attend);
                                        }
                                    }
                                }
                                else{
                                    if (data.getKey().equals(attendance.getfKey())) {
                                        Attendance new_attend = new Attendance(attendance.getDate(),
                                                attendance.getfKey(),
                                                data.child("info").getValue().toString(),
                                                data.child("photo").getValue().toString(),
                                                attendance.getEnterDate(),
                                                attendance.getExitDate());
                                        attendances.add(new_attend);
                                    }
                                }
                            }
                        }
                        progressBar.setVisibility(View.GONE);
                        if(attendances.size() == 0){
                            noLayout.setVisibility(View.VISIBLE);
                        }
                        else{
                            adapter = new AttendanceAdapter(getActivity(), attendances, dayNames);
                            recyclerView.setAdapter(adapter);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        menu.clear();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.sign_out) {

            auth.signOut();
            startActivity(new Intent(getActivity(), LoginPage.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    DateFormat firebaseDateFormat;
    String today;


    public void manageDate() {
        firebaseDateFormat = new SimpleDateFormat("dd_MM_yy");//2001.07.04
        today = firebaseDateFormat.format(Calendar.getInstance().getTime());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    private boolean checkInternetConnection() {

        if (isNetworkAvailable()) {
            return true;
        } else {
            Toast.makeText(getActivity(), getResources().getString(R.string.inetConnection), Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();

    }
//qwe123

    /*

    admin@euro.kz
    qwe123
     */
    public String getFullDayName(String date) {
//        date = date.replace(".","_");
        String dateSplit[] = date.split("_"); //15_01_19

        int year = Integer.parseInt("20" + dateSplit[2]);
        int month = Integer.parseInt(dateSplit[1]) - 1;
        int day = Integer.parseInt(dateSplit[0]);

        calendar.set(year, month, day);

        String dayName = dayNames[calendar.get(Calendar.DAY_OF_WEEK)];
        return dayName;
    }

    public void checkVersion() {
        Query myTopPostsQuery = mDatabaseRef.child("user_ver");
        myTopPostsQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String newVersion = dataSnapshot.getValue().toString();
                    if (!getDayCurrentVersion().equals(newVersion)) {
                        updateVersion(newVersion);
                        new GetUsersFromFirebase().execute();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    String curVer;

    public String getDayCurrentVersion() {
        Cursor res = sqdb.rawQuery("SELECT user_ver FROM versions ", null);
        res.moveToNext();
        curVer = res.getString(0);
        return curVer;
    }

    public void updateVersion(String newVersion) {
        ContentValues versionValues = new ContentValues();
        versionValues.put("user_ver", newVersion);
        sqdb.update("versions", versionValues, "user_ver=" + curVer, null);
    }

    public class GetUsersFromFirebase extends AsyncTask<Void, User, Void> {

        public GetUsersFromFirebase() {

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            mDatabaseRef.child("user_list").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        storeDb.cleanUsers(sqdb);

                        for (DataSnapshot usersSnapshot : dataSnapshot.getChildren()) {
                            User user = usersSnapshot.getValue(User.class);

                            ContentValues userValue = new ContentValues();

                            userValue.put(COLUMN_FKEY, user.getFkey());
                            userValue.put(COLUMN_INFO, user.getInfo());
                            userValue.put(COLUMN_CARD_NUMBER, user.getCardNumber());
                            userValue.put(COLUMN_PHOTO, user.getPhoto());
                            userValue.put(COLUMN_PHONE, user.getPhoneNumber());
                            userValue.put(COLUMN_F_PHOTO_NAME, user.getfPhotoName());

                            sqdb.insert(TABLE_USER, null, userValue);
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            return null;
        }

        @Override
        protected void onProgressUpdate(User... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

}
