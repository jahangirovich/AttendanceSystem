package com.example.ainurbayanova.attendancesystem.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.ainurbayanova.attendancesystem.R;
import com.example.ainurbayanova.attendancesystem.interfaces.ItemClickListener;
import com.example.ainurbayanova.attendancesystem.modules.Notes;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.MyTViewHolder> {
    private Context context;
    private List<Notes> noteList;
    DateFormat dateF;
    String date;
    ItemClickListener itemClickListener;

    public class MyTViewHolder extends RecyclerView.ViewHolder{
        TextView title;
        TextView desc;
        LinearLayout linearLayout;
        public MyTViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.title);
            desc = view.findViewById(R.id.desc);
            linearLayout = view.findViewById(R.id.backgroundNote);
        }

    }

    public NoteAdapter(Context context, ArrayList<Notes> noteList) {
        this.context = context;
        this.noteList = noteList;
    }

    @Override
    public MyTViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.design_of_note, parent, false);
        manageDate();
        return new MyTViewHolder(itemView);
    }


    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(MyTViewHolder holder, int position) {
        Notes item = noteList.get(position);
        holder.title.setText(item.getTitle());
        holder.desc.setText(item.getDesc());
        if(position % 2 == 0){
            holder.linearLayout.setBackgroundColor(context.getResources().getColor(R.color.blueOfNote));
        }
        if(position % 2 == 1){
            holder.linearLayout.setBackgroundColor(context.getResources().getColor(R.color.olivki));
        }
        if(position % 3 == 0){
            holder.linearLayout.setBackgroundColor(context.getResources().getColor(R.color.greenOfNote));
        }
        if(position % 4 == 0){
            holder.linearLayout.setBackgroundColor(context.getResources().getColor(R.color.yellowOfNote));
        }
    }

    public void manageDate() {
        dateF = new SimpleDateFormat("dd.MM");//2001.07.04
        date = dateF.format(Calendar.getInstance().getTime());
    }

    @Override
    public int getItemCount() {
        return noteList.size();
    }

}