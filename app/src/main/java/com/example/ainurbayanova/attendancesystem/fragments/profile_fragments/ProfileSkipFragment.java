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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ProfileSkipFragment extends Fragment {
    View view;
    CarouselView carouselView;
    TextView name;
    TextView desc;
    TextView place;
    StoreDatabase storeDb;
    SQLiteDatabase sqdb;
    StorageReference storageReference;
    DatabaseReference mDatabase;
    ArrayList<Attendance> attendance_list;
    ProgressBar progressBarLoading;
    RecyclerView attendanceRecyclerView;
    String uId;
    String monthList[], dayNames[],monthListInEnglish[];
    String monthName,today;
    ProgressBar circularProgressBar;
    TextView percentage;
    int counter = 0;
    TextView limit;
    int my_day = 0;
    Calendar calendar;
    LinearLayout linearLayout;
    User user;
    DateFormat firebaseDateFormat;
    ProfileAttendanceAdapter profileAttendanceAdapter;
    ArrayList<Attendance> missed_days =new ArrayList<>();
    HashMap<Integer,Attendance> hashSet = new HashMap<>();
    TextView my_date;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_skip_fragment, container, false);
        initWidgets();
        user = (User) getArguments().getSerializable("user");
        managedDate();
        uploadFromDatabase();
        return view;
    }

    public void initWidgets() {
        storeDb = new StoreDatabase(getActivity());
        sqdb = storeDb.getWritableDatabase();
        calendar = Calendar.getInstance();

        storageReference = FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        linearLayout = view.findViewById(R.id.all);
        limit = view.findViewById(R.id.limit);

        attendance_list = new ArrayList<>();

        progressBarLoading = view.findViewById(R.id.progressBarLoading);
        profileAttendanceAdapter = new ProfileAttendanceAdapter(getActivity(),missed_days);
        attendanceRecyclerView = view.findViewById(R.id.attendanceRecyclerView);
        attendanceRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        monthList = getResources().getStringArray(R.array.monthsList);
        monthListInEnglish = getResources().getStringArray(R.array.monthsListInEnglish);
        circularProgressBar = view.findViewById(R.id.circularProgressBar);
        my_date = view.findViewById(R.id.date);
        percentage = view.findViewById(R.id.percentage);
        dayNames = getResources().getStringArray(R.array.dayNames);

        manageDate();
    }

    public void computeUserAttendance(){
        String[] day = today.split("_");
        my_day = Integer.parseInt(day[0]);
        for (int x = 1;x < Integer.parseInt(day[0]);x++){
            if(getFullDayNameForLoop(x,today).equals("Суббота")){
                my_day--;
            }
            else if(getFullDayNameForLoop(x,today).equals("Воскресенье")){
                my_day--;
            }
            else{
                for (Attendance attendance:attendance_list){
                    String[] myDate = attendance.getDate().split("_");
                    if(x == Integer.parseInt(myDate[0])){
                        counter++;
                    }
                    if(x != Integer.parseInt(myDate[0])){
//                        missed_days.add(attendance);
                    }
                }
            }
        }

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
                linearLayout.setVisibility(View.VISIBLE);
                progressBarLoading.setVisibility(View.GONE);
                computeUserAttendance();
                profileAttendanceAdapter.notifyDataSetChanged();
                attendanceRecyclerView.setAdapter(profileAttendanceAdapter);
                setAllCredentialsForPercent();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void setAllCredentialsForPercent(){
        percentage.setText(counter * 100 / my_day + "%");
        circularProgressBar.setProgress(counter * 100/my_day);
        limit.setText(counter + " days "+ "participants in " + my_day + " day.");
    }

//    attendance@euro.kz
//    123123
//admin@euro.kz
//    qwe123

    public void managedDate() {
        firebaseDateFormat = new SimpleDateFormat("dd_MM_yy");//2001.07.04
        DateFormat firebaseDateFormat2 = new SimpleDateFormat("dd/MM/yy");//2001.07.04
        today = firebaseDateFormat.format(Calendar.getInstance().getTime());
        my_date.setText("Date:" + firebaseDateFormat2.format(Calendar.getInstance().getTime()) + "\n\n" +
                "Please mention compution will go in one month ");
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

    public String getFullDayNameInEnglish(String date) {
        String dateSplit[] = date.split("_"); //15_01_19

        int year = Integer.parseInt("20" + dateSplit[2]);
        int month = Integer.parseInt(dateSplit[1]) - 1;
        int day = Integer.parseInt(dateSplit[0]);

        calendar.set(year, month, day);
        //            calendar.set(2019, 0, 17, 0, 0, 0);

        String dayName = monthListInEnglish[calendar.get(Calendar.DAY_OF_WEEK)];
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

    DateFormat dateF;
    String date, curMonthNumber, monthN;

    public void manageDate() {
        dateF = new SimpleDateFormat("dd_MM_yy");//01.12.19
        date = dateF.format(Calendar.getInstance().getTime());
        curMonthNumber = date.split("_")[1];

        monthName = monthList[Integer.parseInt(date.split("_")[1]) - 1];
    }
    //    attendance@euro.kz
//    123123
//admin@euro.kz
//    qwe123

    public class ProfileAttendanceAdapter extends RecyclerView.Adapter<ProfileSkipFragment.ProfileAttendanceAdapter.MyTViewHolder> {
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
        public ProfileSkipFragment.ProfileAttendanceAdapter.MyTViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView;
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.profile_attendance_design, parent, false);
            return new ProfileSkipFragment.ProfileAttendanceAdapter.MyTViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(ProfileSkipFragment.ProfileAttendanceAdapter.MyTViewHolder holder, int position) {

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
