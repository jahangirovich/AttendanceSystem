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
import com.example.ainurbayanova.attendancesystem.activities.AttendanceActivity;
import com.example.ainurbayanova.attendancesystem.modules.Attendance;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;

import de.hdodenhof.circleimageview.CircleImageView;

public class AttendanceAdapter extends RecyclerView.Adapter<AttendanceAdapter.MyTViewHolder> {
    private Context context;
    private ArrayList<Attendance> userList;
    Calendar calendar;
    String[] dayNames;

    public AttendanceAdapter(Context context, ArrayList<Attendance> userSet, String[] dayNames) {
        this.context = context;
        this.dayNames = dayNames;
        this.userList = userSet;
        calendar = Calendar.getInstance();
//        dayNames = context.getResources().getStringArray(R.array.dayNames);
    }


    public class MyTViewHolder extends RecyclerView.ViewHolder {
        CircleImageView imageView;
        TextView name;
        TextView time;
        TextView enterDate, exitDate;

        public MyTViewHolder(View view) {
            super(view);
            imageView = view.findViewById(R.id.imageView);
            name = view.findViewById(R.id.name);
            time = view.findViewById(R.id.time);
            enterDate = view.findViewById(R.id.enterDate);
            exitDate = view.findViewById(R.id.exitDate);
        }
    }

    @Override
    public MyTViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_attendance, parent, false);
        return new MyTViewHolder(itemView);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(MyTViewHolder holder, int position) {

        Attendance item = userList.get(position);
        String enterDateStr = item.getEnterDate();
        String exitDateStr = item.getExitDate();

//        Log.i("firee","name: "+item.getfKey());

        holder.name.setText(item.getInfo());
        holder.enterDate.setText(enterDateStr);
        holder.exitDate.setText(exitDateStr);

        String[] enterSlip = enterDateStr.split(":");

        int enterHour = 0;
        int enterMinute = 0;
        int exitHour = 0;
        int exitMinute = 0;

        int enterTimeHour = 8;//08:30
        int exitTimeHour = 15;//08:30
        int enterTimeMinute = 30;//08:30
        int myHour = Integer.parseInt(enterSlip[0]);
        int myMinute = Integer.parseInt(enterSlip[1]);
        int totalHour = 0;
        int totalMinute = 0;

        if((myHour * 60 + myMinute <= enterTimeHour * 60 + 30) && exitDateStr.equals("no")){
            enterHour = enterTimeHour - myHour;
            enterMinute = enterTimeMinute - myMinute;
            holder.time.setText("He(She) early:"+enterHour + " hour," + enterMinute + "minute.");
        }
        else if((myHour * 60 + myMinute >= enterTimeHour * 60 + 30) && exitDateStr.equals("no")){
            enterHour = myHour - enterTimeHour;
            enterMinute = myMinute - enterTimeMinute;
            holder.time.setText("He(She) late:"+enterHour + " hour," + enterMinute + "minute.");
        }

        else if((myHour * 60 + myMinute <= enterTimeHour * 60 + 30) && !exitDateStr.equals("no")){
            enterHour = enterTimeHour - myHour;
            enterMinute = enterTimeMinute - myMinute;
            String[] myExit = exitDateStr.split(":");
            int myExitHour = Integer.parseInt(myExit[0]);
            int myExitMinute = Integer.parseInt(myExit[1]);
            if(myExitHour * 60 + myExitMinute <= exitTimeHour * 60 + 35){
                totalHour = exitTimeHour - myExitHour;
                totalMinute = 35 - myExitMinute;
                holder.time.setText("Early:"+enterHour + " hour," + enterMinute + "minute." + "\n"
                + "Early: " + totalHour + " hour," + totalMinute + "minute.");
            }
            else if(myExitHour * 60 + myExitMinute >= exitTimeHour * 60 + 35){
                totalHour = myExitHour - exitTimeHour;
                totalMinute = myExitMinute - 35;
                holder.time.setText("Early:"+enterHour + " hour," + enterMinute + "minute." + "\n"
                        + "Late: " + totalHour + " hour," + totalMinute + "minute.");
            }
        }

        else if((myHour * 60 + myMinute >= enterTimeHour * 60 + 30) && !exitDateStr.equals("no")){
            enterHour = myHour - enterTimeHour;
            enterMinute = myMinute - enterTimeMinute;
            String[] myExit = exitDateStr.split(":");
            int myExitHour = Integer.parseInt(myExit[0]);
            int myExitMinute = Integer.parseInt(myExit[1]);
            if(myExitHour * 60 + myExitMinute <= exitTimeHour * 60 + 35){
                totalHour = exitTimeHour - myExitHour;
                totalMinute = 35 - myExitMinute;
                holder.time.setText("Late:"+enterHour + " hour," + enterMinute + "minute." + "\n"
                        + "Early: " + totalHour + " hour," + totalMinute + "minute.");
            }
            else if(myExitHour * 60 + myExitMinute >= exitTimeHour * 60 + 35){
                totalHour = myExitHour - exitTimeHour;
                totalMinute = myExitMinute - 35;
                holder.time.setText("Late:"+enterHour + " hour," + enterMinute + "minute." + "\n"
                        + "Late: " + totalHour + " hour," + totalMinute + "minute.");
            }
        }

        Glide.with(context)
                .load(item.getPhoto())
                .placeholder(R.drawable.man)
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
        return userList.size();
    }

}