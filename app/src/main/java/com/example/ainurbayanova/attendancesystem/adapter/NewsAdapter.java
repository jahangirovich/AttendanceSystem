package com.example.ainurbayanova.attendancesystem.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.ainurbayanova.attendancesystem.R;
import com.example.ainurbayanova.attendancesystem.interfaces.ItemClickListener;
import com.example.ainurbayanova.attendancesystem.modules.News;
import com.example.ainurbayanova.attendancesystem.modules.Notes;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.MyTViewHolder> {
    private Context context;
    private List<News> newsArrayList;
    DateFormat dateF;
    String date;
    ItemClickListener itemClickListener;

    public class MyTViewHolder extends RecyclerView.ViewHolder{
        TextView title;
        TextView text;
        TextView date;
        ImageView imageView;
        public MyTViewHolder(final View view) {
            super(view);
            title = view.findViewById(R.id.title);
            text = view.findViewById(R.id.text);
            imageView = view.findViewById(R.id.imageView);
            date = view.findViewById(R.id.date);
            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    itemClickListener.onItemLongClick(view,getAdapterPosition());

                    return false;
                }
            });
        }

    }

    public NewsAdapter(Context context, ArrayList<News> newsArrayList) {
        this.context = context;
        this.newsArrayList = newsArrayList;
    }

    @Override
    public MyTViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.design_news, parent, false);
        manageDate();
        return new MyTViewHolder(itemView);
    }

    public void setItemClickListener(ItemClickListener itemClickListener){
        this.itemClickListener = itemClickListener;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(MyTViewHolder holder, int position) {
        News news = newsArrayList.get(position);
        if(!news.getPhoto().equals("no photo")){
            Glide.with(context)
                    .load(news.getPhoto())
                    .into(holder.imageView);
        }
        holder.date.setText(news.getDate());
        holder.text.setText(news.getText());
        holder.title.setText(news.getTitle());
    }

    public void manageDate() {
        dateF = new SimpleDateFormat("dd.MM");//2001.07.04
        date = dateF.format(Calendar.getInstance().getTime());
    }

    @Override
    public int getItemCount() {
        return newsArrayList.size();
    }

}