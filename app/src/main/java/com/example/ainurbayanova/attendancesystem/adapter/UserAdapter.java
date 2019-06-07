package com.example.ainurbayanova.attendancesystem.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.ainurbayanova.attendancesystem.R;
import com.example.ainurbayanova.attendancesystem.activities.Profile;
import com.example.ainurbayanova.attendancesystem.interfaces.ItemClickListener;
import com.example.ainurbayanova.attendancesystem.modules.User;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.MyTViewHolder> {
    private Context context;
    private List<User> noteList;
    ItemClickListener clickListener;
    public class MyTViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener {
        CircleImageView imageView;
        TextView name;
        TextView phoneNumber;
        TextView groupName;

        public MyTViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.name);
            phoneNumber = view.findViewById(R.id.phoneNumber);
            groupName = view.findViewById(R.id.groupName);
            imageView = view.findViewById(R.id.imageView);
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);

        }

        @Override
        public void onClick(View view) {
            clickListener.onItemClick(view,getLayoutPosition());
        }

        @Override
        public boolean onLongClick(View v) {
            clickListener.onItemLongClick(v,getLayoutPosition());
            return true;
        }
    }

    public void setOnClick(ItemClickListener clickListener){
        this.clickListener = clickListener;
    }

    public UserAdapter(Context context, List<User> noteList) {
        this.context = context;
        this.noteList = noteList;
    }

    @Override
    public MyTViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_users, parent, false);

        return new MyTViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyTViewHolder holder, int position) {

        final User item = noteList.get(position);
        holder.name.setText(item.getInfo());
        holder.phoneNumber.setText(item.getPhoneNumber());
        holder.groupName.setText(item.getSpinner());

        if(item.getPhoto().equals("no photo")){
            Glide.with(context)
                    .load(item.getPhoto())
                    .placeholder(R.drawable.student)
                    .into(holder.imageView);
        }
        else{
            Glide.with(context)
                    .load(item.getPhoto())
                    .into(holder.imageView);
        }
    }

    @Override
    public int getItemCount() {
        return noteList.size();
    }

    private boolean checkInternetConnection() {

        if (isNetworkAvailable()) {
            return true;
        } else {
            Toast.makeText(context, context.getResources().getString(R.string.inetConnection), Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();

    }

}