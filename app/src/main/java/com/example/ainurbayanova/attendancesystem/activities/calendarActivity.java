package com.example.ainurbayanova.attendancesystem.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CalendarView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.ainurbayanova.attendancesystem.MainActivity;
import com.example.ainurbayanova.attendancesystem.R;
import com.example.ainurbayanova.attendancesystem.adapter.AttendanceAdapter;
import com.example.ainurbayanova.attendancesystem.adapter.AttendanceInCalendar;
import com.example.ainurbayanova.attendancesystem.interfaces.UserFirebaseInterface;
import com.example.ainurbayanova.attendancesystem.interfaces.UserStoriesInterface;
import com.example.ainurbayanova.attendancesystem.modules.Attendance;
import com.example.ainurbayanova.attendancesystem.modules.Entered;
import com.example.ainurbayanova.attendancesystem.modules.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class calendarActivity extends AppCompatActivity {
    CalendarView calendarView;
    Toolbar toolbar;
    RecyclerView recyclerView;
    ArrayList<Attendance> attendanceArrayList = new ArrayList<>();
    ArrayList<Attendance> new_attendanceArrayList = new ArrayList<>();
    LinearLayout showResult;
    ProgressBar circularProgressBar;
    TextView amount;
    TextView percent;
    RelativeLayout noLayout;
    ProgressBar progressBar;
    DatabaseReference databaseReference;
    RelativeLayout all;
    AttendanceInCalendar attendanceInCalendar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        initToolbar();
        initWidgets();
        initCalendar();
        initAllFromFirebase(getDay(),getMonth());
        Log.i("info",getMonth() + "");
    }

    private void initToolbar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initWidgets() {
        calendarView = findViewById(R.id.materialCalendarView);
        recyclerView = findViewById(R.id.recyclerView);
        all = findViewById(R.id.all);
        showResult = findViewById(R.id.showResult);
        noLayout = findViewById(R.id.noLayout);
        percent = findViewById(R.id.percentage);
        progressBar = findViewById(R.id.progressBar);
        circularProgressBar = findViewById(R.id.circularProgressBar);
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    public void initCalendar(){
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, final int year, final int month, final int dayOfMonth) {
                initAllFromFirebase(dayOfMonth,month);
            }
        });
//        addToThatDay(getDay(),getMonth()-1);
    }

    public void initAllFromFirebase(final int dayOfMonth, final int month){
        all.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        databaseReference.child("attendance").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                attendanceArrayList.clear();
                for (DataSnapshot data:dataSnapshot.getChildren()){
                    String[] splittedData = data.getKey().split("_");
                    if(Integer.parseInt(splittedData[0]) == dayOfMonth &&
                            Integer.parseInt(splittedData[1]) == month + 1){
                        for (DataSnapshot dating:data.getChildren()){
                            Entered entered = dating.child("entered").getValue(Entered.class);
                            Entered closed = dating.child("closed").getValue(Entered.class);
                            Attendance attendance = new Attendance(dating.getKey(),entered,closed);
                            attendanceArrayList.add(attendance);
                        }
                    }
                }
                databaseReference.child("user_list").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        new_attendanceArrayList.clear();
                        for (DataSnapshot data:dataSnapshot.getChildren()){
                            for (Attendance attendance:attendanceArrayList){
                                if(data.getKey().equals(attendance.getfKey())){
                                    String date = dayOfMonth + "_" + month;
                                    Attendance new_attendance = new Attendance(date,
                                            data.getKey(),
                                            data.child("info").getValue().toString(),
                                            data.child("photo").getValue().toString(),
                                            data.child("spinner").getValue().toString(),
                                            attendance.getEntered(),
                                            attendance.getClosed());
                                    new_attendanceArrayList.add(new_attendance);
                                }
                            }
                        }
                        all.setVisibility(View.VISIBLE);
                        if(new_attendanceArrayList.size() == 0){
                            progressBar.setVisibility(View.GONE);
                            showResult.setVisibility(View.GONE);
                            noLayout.setVisibility(View.VISIBLE);
                        }
                        else{
                            progressBar.setVisibility(View.GONE);
                            all.setVisibility(View.VISIBLE);
                            noLayout.setVisibility(View.GONE);
                            showResult.setVisibility(View.VISIBLE);
                            attendanceInCalendar = new AttendanceInCalendar(calendarActivity.this,new_attendanceArrayList);
                            recyclerView.setAdapter(attendanceInCalendar);
                            recyclerView.setLayoutManager(new LinearLayoutManager(calendarActivity.this));
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
//
//    public void addToThatDay(int dayOfMonth,int month){
//        for (Attendance challenge_done:A){
//            int my_day = challenge_done.getDate().getDay();
//            int my_month = challenge_done.getDate().getMonth();
//            if(dayOfMonth == my_day && my_month == (month + 1)){
//                date_challenges.add(challenge_done);
//            }
//        }
//        if(date_challenges.size() != 0){
//            initRecyclerChallenge();
//            showResult.setVisibility(View.VISIBLE);
//            setProgressResults();
//        }
//        else{
//            noLayout.setVisibility(View.VISIBLE);
//        }
//    }


    public int getMonth() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat dateformat = new SimpleDateFormat("M");
        int month = Integer.parseInt(dateformat.format(c.getTime()));
        return month;
    }

    public int getDay() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat dateformat = new SimpleDateFormat("d");
        int day = Integer.parseInt(dateformat.format(c.getTime()));
        return day;
    }

}
