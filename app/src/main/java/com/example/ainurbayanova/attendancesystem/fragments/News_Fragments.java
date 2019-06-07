package com.example.ainurbayanova.attendancesystem.fragments;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ainurbayanova.attendancesystem.R;
import com.example.ainurbayanova.attendancesystem.adapter.NewsAdapter;
import com.example.ainurbayanova.attendancesystem.interfaces.ItemClickListener;
import com.example.ainurbayanova.attendancesystem.modules.News;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class News_Fragments extends Fragment {

    View view;
    RecyclerView recyclerView;
    ProgressBar progressBar;
    TextView textView;
    NewsAdapter newsAdapter;
    DatabaseReference databaseReference;
    ArrayList<News> newsArrayList = new ArrayList<>();
    FirebaseUser firebaseUser;
    TextView showNotAdmin;
    LinearLayout all;
    public News_Fragments() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_news,container,false);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        initWidgets();
        uploadFromFirebase();
        clickListener();

        return view;
    }

    public void clickListener(){
        newsAdapter.setItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(View v, int pos) {

            }

            @Override
            public void onItemLongClick(View v, int pos) {
                initDialog(pos);
            }
        });
    }

    public void initDialog(final int position){
        LayoutInflater factory = LayoutInflater.from(getActivity());

        final View listDialog = factory.inflate(R.layout.dialog_of_menu, null);
        final AlertDialog.Builder alertTimeDialog = new AlertDialog.Builder(getActivity());
        RelativeLayout deleteLayout = listDialog.findViewById(R.id.remove);
        alertTimeDialog.setView(listDialog);
        final AlertDialog hi = alertTimeDialog.show();

        deleteLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                yesOrNo(position);
                hi.dismiss();
            }
        });
    }

    public void yesOrNo(final int position){
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        databaseReference.child("news").child(newsArrayList.get(position).getFkey()).removeValue();
                        Toast.makeText(getActivity(),"Removed!",Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        newsAdapter.notifyDataSetChanged();
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Are your sure").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }

    public void initWidgets(){
        recyclerView = view.findViewById(R.id.recyclerView);
        progressBar = view.findViewById(R.id.progressBar);
        textView = view.findViewById(R.id.textView);
        newsAdapter = new NewsAdapter(getActivity(),newsArrayList);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        showNotAdmin = view.findViewById(R.id.showNotAdmin);
        all = view.findViewById(R.id.all);
        if(firebaseUser.getEmail().equals("check@sdcl.kz")){
            all.setVisibility(View.GONE);
            showNotAdmin.setVisibility(View.VISIBLE);
        }
        else{
            all.setVisibility(View.VISIBLE);
            showNotAdmin.setVisibility(View.GONE);
        }
    }

    private void uploadFromFirebase(){
        databaseReference.child("news").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                newsArrayList.clear();
                for (DataSnapshot data:dataSnapshot.getChildren()){
                    News news = data.getValue(News.class);
                    newsArrayList.add(news);
                }
                initRecycler();
                if(newsArrayList.size() == 0){
                    progressBar.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.GONE);
                    textView.setVisibility(View.VISIBLE);
                }else{
                    progressBar.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    textView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void initRecycler(){
        newsAdapter.notifyDataSetChanged();
        recyclerView.setAdapter(newsAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }
}
