package com.example.ainurbayanova.attendancesystem.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ainurbayanova.attendancesystem.R;
import com.example.ainurbayanova.attendancesystem.fragments.profile_fragments.ProfileAttendanceFragment;
import com.example.ainurbayanova.attendancesystem.modules.Attendance;

import java.util.Calendar;
import java.util.List;

public class ProfileAttendanceAdapter extends RecyclerView.Adapter<ProfileAttendanceAdapter.MyTViewHolder> {
    private Context context;
    private List<Attendance> noteList;
    Calendar calendar;
    String[] dayNames;

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
    public ProfileAttendanceAdapter.MyTViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.profile_attendance_design, parent, false);
        dayNames = context.getResources().getStringArray(R.array.dayNamesInEnglish);
        return new ProfileAttendanceAdapter.MyTViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ProfileAttendanceAdapter.MyTViewHolder holder, int position) {

        Attendance item = noteList.get(position);
        holder.dayOfWeek.setText(getFullDayName(item.getDate()) + ":");
        holder.date.setText(item.getDate());
        holder.enterDate.setText(item.getEntered().getTime());
        if(item.getEntered().getAction().equals("late")){
            holder.enteredAction.setBackgroundColor(context.getResources().getColor(R.color.granate));
            holder.enteredAction.setText("Late");
        }
        else{
            holder.enteredAction.setBackgroundColor(context.getResources().getColor(R.color.green));
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
