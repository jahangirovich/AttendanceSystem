package com.example.ainurbayanova.attendancesystem.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.ainurbayanova.attendancesystem.R;
import com.example.ainurbayanova.attendancesystem.modules.Attendance;

import java.util.ArrayList;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class AttendanceInCalendar extends RecyclerView.Adapter<AttendanceInCalendar.MyTViewHolder> {
    private Context context;
    private ArrayList<Attendance> attendances;
    Calendar calendar;
    String[] dayNames;

    public AttendanceInCalendar(Context context, ArrayList<Attendance> attendances) {
        this.context = context;
        this.attendances = attendances;
        calendar = Calendar.getInstance();
//        dayNames = context.getResources().getStringArray(R.array.dayNames);
    }


    public class MyTViewHolder extends RecyclerView.ViewHolder {
        CircleImageView imageView;
        TextView name;
        TextView enterDate, exitDate,group;

        public MyTViewHolder(View view) {
            super(view);
            imageView = view.findViewById(R.id.circleImage);
            name = view.findViewById(R.id.name);
            enterDate = view.findViewById(R.id.enterDate);
            exitDate = view.findViewById(R.id.exitDate);
            group = view.findViewById(R.id.group);
        }
    }

    @Override
    public MyTViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.design_attendance, parent, false);
        return new MyTViewHolder(itemView);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(MyTViewHolder holder, int position) {

        Attendance item = attendances.get(position);
        String enterDateStr = "";
        String exitDateStr = "";
        if(item.getClosed() != null){
            exitDateStr = item.getClosed().getTime();
        }
        else{


        }
        if(item.getEntered() != null){
            enterDateStr = item.getEntered().getTime();
        }
        else{
        }

//        Log.i("firee","name: "+item.getfKey());

        holder.name.setText(item.getInfo());
        holder.group.setText(item.getGroup());
        holder.enterDate.setText(enterDateStr);
        holder.exitDate.setText(exitDateStr);

        Glide.with(context)
                .load(item.getPhoto())
                .placeholder(R.drawable.student)
                .into(holder.imageView);
    }

    public String getFullDayName(String date) {
        String dateSplit[] = date.split("_"); //15_01_19

        int year = Integer.parseInt("20" + dateSplit[2]);
        int month = Integer.parseInt(dateSplit[1]) - 1;
        int day = Integer.parseInt(dateSplit[0]);

        calendar.set(year, month, day);

        String dayName = dayNames[calendar.get(Calendar.DAY_OF_WEEK)];
        return dayName;
    }

    @Override
    public int getItemCount() {
        return attendances.size();
    }

}
