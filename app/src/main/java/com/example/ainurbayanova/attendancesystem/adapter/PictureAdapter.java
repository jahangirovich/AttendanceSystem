package com.example.ainurbayanova.attendancesystem.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.ainurbayanova.attendancesystem.R;

import java.util.ArrayList;



public class PictureAdapter extends RecyclerView.Adapter<PictureAdapter.MyTViewHolder> {
    ArrayList<Uri> uris = new ArrayList<>();
    Context context;
    public PictureAdapter(Context context, ArrayList<Uri> uris){
        this.context = context;
        this.uris = uris;
    }
    @NonNull
    @Override
    public MyTViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView;
        itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_users, viewGroup, false);
        return new MyTViewHolder(itemView);
    }

    @Override
    public int getItemCount() {
        return uris.size();
    }

    public class MyTViewHolder extends RecyclerView.ViewHolder{
        ImageView imageViews;
        LinearLayout linearLayout;

        public MyTViewHolder(View view) {
            super(view);
            imageViews = view.findViewById(R.id.imageViews);
            linearLayout = view.findViewById(R.id.linearLayout);
            linearLayout.setVisibility(View.GONE);
            imageViews.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull PictureAdapter.MyTViewHolder myTViewHolder, int i) {
        if(uris.get(i) != null){

            myTViewHolder.imageViews.setImageURI(uris.get(i));

        }
    }
}
