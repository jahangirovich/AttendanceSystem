package com.example.ainurbayanova.attendancesystem.fragments.profile_fragments;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.ainurbayanova.attendancesystem.R;
import com.example.ainurbayanova.attendancesystem.activities.Profile;
import com.example.ainurbayanova.attendancesystem.database.StoreDatabase;
import com.example.ainurbayanova.attendancesystem.modules.Attendance;
import com.example.ainurbayanova.attendancesystem.modules.Entered;
import com.example.ainurbayanova.attendancesystem.modules.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.synnapps.carouselview.CarouselView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileAttendanceFragment extends Fragment {
    View view;
    StoreDatabase storeDb;
    SQLiteDatabase sqdb;
    ProfileAttendanceAdapter profileAttendanceAdapter;
    StorageReference storageReference;
    DatabaseReference mDatabase;
    ArrayList<Attendance> attendance_list = new ArrayList<>();
    ProgressBar progressBarLoading;
    RecyclerView attendanceRecyclerView;
    String uId;
    DateFormat dateF;
    String date, curMonthNumber;
    TextView noLayout;
    String monthList[], dayNames[];
    Calendar calendar;
    String monthName;
    User user;
    String today;
    DateFormat firebaseDateFormat;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profile_attendance, container, false);
        initWidgets();
        user = (User) getArguments().getSerializable("user");
        uploadFromDatabase();
        managedDate();
        computeUserAttendance();
        return view;
    }

    private void uploadFromDatabase() {
        mDatabase.child("attendance").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot data:dataSnapshot.getChildren()){
                    for (DataSnapshot keys:data.getChildren()){
                        if(keys.getKey().equals(user.getFkey())){
                            Entered entered = keys.child("entered").getValue(Entered.class);
                            Entered closed = keys.child("closed").getValue(Entered.class);
                            Attendance attendance = new Attendance(
                                 data.getKey(),
                                 keys.getKey(),
                                 user.getInfo(),
                                 user.getPhoto(),
                                 user.getSpinner(),
                                 entered,
                                 closed
                            );
                            attendance_list.add(attendance);
                        }
                    }
                }
                if(attendance_list.size() == 0){
                    progressBarLoading.setVisibility(View.GONE);
                    noLayout.setVisibility(View.VISIBLE);
                }
                else{
                    progressBarLoading.setVisibility(View.GONE);
                    attendanceRecyclerView.setVisibility(View.VISIBLE);
                    initRecycler();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void initWidgets() {
        storeDb = new StoreDatabase(getActivity());
        sqdb = storeDb.getWritableDatabase();
        storageReference = FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        calendar = Calendar.getInstance();
        noLayout = view.findViewById(R.id.noLayout);

        attendance_list = new ArrayList<>();

        progressBarLoading = view.findViewById(R.id.progressBarLoading);
        attendanceRecyclerView = view.findViewById(R.id.attendanceRecyclerView);
        attendanceRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        monthList = getResources().getStringArray(R.array.monthsList);
        dayNames = getResources().getStringArray(R.array.dayNamesInEnglish);
        profileAttendanceAdapter = new ProfileAttendanceAdapter(getActivity(),attendance_list);

        manageDate();
    }

    public void computeUserAttendance(){
        String[] day = today.split("_");
        for (int x = 1;x < Integer.parseInt(day[0]);x++){
            if(getFullDayNameForLoop(x,today).equals("Суббота")){
                continue;
            }
            else if(getFullDayNameForLoop(x,today).equals("Воскресенье")){
                continue;
            }
            else{

            }
        }
    }

    public void manageDate() {
        dateF = new SimpleDateFormat("dd_MM_yy");//01.12.19
        date = dateF.format(Calendar.getInstance().getTime());
        curMonthNumber = date.split("_")[1];
        monthName = monthList[Integer.parseInt(date.split("_")[1]) - 1];
    }
    public void managedDate() {
        firebaseDateFormat = new SimpleDateFormat("dd_MM_yy");//2001.07.04
        today = firebaseDateFormat.format(Calendar.getInstance().getTime());
    }

    public String getFullDayName(String date) {
        String dateSplit[] = date.split("_"); //15_01_19

        int year = Integer.parseInt("20" + dateSplit[2]);
        int month = Integer.parseInt(dateSplit[1]) - 1;
        int day = Integer.parseInt(dateSplit[0]);

        calendar.set(year, month, day);
    //            calendar.set(2019, 0, 17, 0, 0, 0);

        String dayName = dayNames[calendar.get(Calendar.DAY_OF_WEEK)];
        return dayName;
    }

    public String getFullDayNameForLoop(int x , String date) {
        String dateSplit[] = date.split("_"); //15_01_19

        int year = Integer.parseInt("20" + dateSplit[2]);
        int month = Integer.parseInt(dateSplit[1]) - 1;
        int day = Integer.parseInt(dateSplit[0]);

        calendar.set(year, month, x);
        //            calendar.set(2019, 0, 17, 0, 0, 0);

        String dayName = dayNames[calendar.get(Calendar.DAY_OF_WEEK)];
        return dayName;
    }

    public void initRecycler(){
        profileAttendanceAdapter.notifyDataSetChanged();
        attendanceRecyclerView.setVisibility(View.VISIBLE);
        attendanceRecyclerView.setAdapter(profileAttendanceAdapter);
    }

    public class ProfileAttendanceAdapter extends RecyclerView.Adapter<ProfileAttendanceAdapter.MyTViewHolder> {
        private Context context;
        private List<Attendance> noteList;
        Calendar calendar;

        public class MyTViewHolder extends RecyclerView.ViewHolder {
            TextView date;
            TextView dayOfWeek;
            TextView enterDate, enteredAction;

            public MyTViewHolder(View view) {
                super(view);
                date = view.findViewById(R.id.date);
                dayOfWeek = view.findViewById(R.id.dayOfWeek);
                enterDate = view.findViewById(R.id.enteredTime);
                enteredAction = view.findViewById(R.id.enteredAction);
            }

        }

        public ProfileAttendanceAdapter(Context context, List<Attendance> noteList) {
            this.context = context;
            this.noteList = noteList;
            calendar = Calendar.getInstance();
        }

        @Override
        public MyTViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView;
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.profile_attendance_design, parent, false);
            return new MyTViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(MyTViewHolder holder, int position) {

            Attendance item = noteList.get(position);
            holder.dayOfWeek.setText(getFullDayName(item.getDate()) + ":");
            holder.date.setText(item.getDate());
            holder.enterDate.setText(item.getEntered().getTime());
            if(item.getEntered().getAction().equals("late")){
                holder.enteredAction.setBackgroundColor(getResources().getColor(R.color.granate));
                holder.enteredAction.setText("Late");
            }
            else{
                holder.enteredAction.setBackgroundColor(getResources().getColor(R.color.green));
                holder.enteredAction.setText("Early");
            }

//            totalLate.setText(hLate +" час "+mLate+" минут");
//            totalEarly.setText(hEarly +" час "+mEarly+" минут");

//            Glide.with(context)
//                    .load(R.drawable.ic_date_range)
//                    .into(holder.imageView);

        }

        public String getFullDayName(String date) {
            String dateSplit[] = date.split("_"); //15_01_19

            int year = Integer.parseInt("20" + dateSplit[2]);
            int month = Integer.parseInt(dateSplit[1]) - 1;
            int day = Integer.parseInt(dateSplit[0]);

            calendar.set(year, month, day);
//            calendar.set(2019, 0, 17, 0, 0, 0);

            String dayName = dayNames[calendar.get(Calendar.DAY_OF_WEEK)];
            return dayName;
        }

        @Override
        public int getItemCount() {
            return noteList.size();
        }

    }
}
