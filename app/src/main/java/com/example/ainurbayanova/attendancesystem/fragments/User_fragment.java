package com.example.ainurbayanova.attendancesystem.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.example.ainurbayanova.attendancesystem.R;
import com.example.ainurbayanova.attendancesystem.activities.Profile;
import com.example.ainurbayanova.attendancesystem.adapter.UserAdapter;
import com.example.ainurbayanova.attendancesystem.interfaces.ItemClickListener;
import com.example.ainurbayanova.attendancesystem.modules.Entered;
import com.example.ainurbayanova.attendancesystem.modules.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;

public class User_fragment extends Fragment {
    View view;
    SearchView searchView;
    ProgressBar progressBar;
    RecyclerView recyclerView;
    DatabaseReference databaseReference;
    ArrayList<User> userArrayList = new ArrayList<>();
    ArrayList<User> userArrayListCopy = new ArrayList<>();
    UserAdapter userAdapter;
    LayoutAnimationController animation;
    SwipeRefreshLayout swipeRefreshLayout;
    LinearLayoutManager linearLayoutManager;
    DividerItemDecoration dividerItemDecoration;
    FirebaseUser firebaseUser;
    DateFormat firebaseDateFormat, timeF;
    String today;
    LinearLayout all;
    TextView userNotAdmin;
    String time;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_user_fragment, container, false);
        ;
        initAll();
        uploadFromDatabase();
        clickListener();
        forSearchView();
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                uploadFromDatabase();
            }
        });
        return view;
    }

    public void uploadFromDatabase() {
        databaseReference.child("user_list").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userArrayList.clear();
                userArrayListCopy.clear();
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    User user = data.getValue(User.class);
                    if (firebaseUser.getEmail().equals("teacher@sdcl.kz")) {
                        if (data.child("spinner").getValue().toString().equals("3-02")) {
                            userArrayList.add(user);
                            userArrayListCopy.add(user);
                        }
                    } else {
                        userArrayList.add(user);
                        userArrayListCopy.add(user);
                    }
                }
                managedDate();
                progressBar.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
                initializeAdapterWithRecycler();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void managedDate() {
        firebaseDateFormat = new SimpleDateFormat("dd_MM_yy");//2001.07.04
        today = firebaseDateFormat.format(Calendar.getInstance().getTime());
    }

    public void initDialog(final int position) {
        LayoutInflater factory = LayoutInflater.from(getActivity());

        final View listDialog = factory.inflate(R.layout.dialog_of_menu, null);
        final AlertDialog.Builder alertTimeDialog = new AlertDialog.Builder(getActivity());
        RelativeLayout deleteLayout = listDialog.findViewById(R.id.remove);
        RelativeLayout clockLayout = listDialog.findViewById(R.id.setLate);
        if (firebaseUser.getEmail().equals("admin@sdcl.kz")) {
            clockLayout.setVisibility(View.GONE);
            deleteLayout.setVisibility(View.VISIBLE);
        } else {
            clockLayout.setVisibility(View.VISIBLE);
            deleteLayout.setVisibility(View.GONE);
        }
        alertTimeDialog.setView(listDialog);
        final AlertDialog hi = alertTimeDialog.show();

        deleteLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                yesOrNo(position);
                hi.dismiss();
            }
        });
        clockLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkIt(position, hi);
//                databaseReference.child("attendance").child(today).child("entered").child(userArrayList.get(position).getFkey()).child("entered");
            }
        });
    }

    public void checkIt(final int x, final Dialog dialog) {
        String[] enterSlip = getCurrentTime().split(":");

        int enterHour = 0;
        int enterMinute = 0;

        int enterTimeHour = 8;//08:30
        int enterTimeMinute = 30;//08:30
        int myHour = Integer.parseInt(enterSlip[0]);
        int myMinute = Integer.parseInt(enterSlip[1]);

        if ((myHour * 60 + myMinute <= enterTimeHour * 60 + 30)) {
            enterHour = enterTimeHour - myHour;
            enterMinute = enterTimeMinute - myMinute;
            final Entered entered = new Entered("early", myHour + ":" + myMinute,false);
            if (!(enterHour > 2)) {
                databaseReference.child("attendance").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        boolean exist = false;
                        for (DataSnapshot data : dataSnapshot.getChildren()) {
                            if (data.getKey().equals(today) && data.child(userArrayList.get(x).getFkey()).exists()) {
                                exist = true;
                            }
                        }
                        if (exist) {
                            Toast.makeText(getActivity(), "Sorry but you already in", Toast.LENGTH_SHORT).show();
                        } else if (!exist) {
                            databaseReference.child("attendance").child(today).child(userArrayList.get(x).getFkey()).child("entered").setValue(entered);
                        }
                        dialog.dismiss();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            } else {
                Toast.makeText(getActivity(), "Sorry but it is too early", Toast.LENGTH_SHORT).show();
            }
            dialog.dismiss();
        } else if ((myHour * 60 + myMinute >= enterTimeHour * 60 + 30)) {
            enterHour = myHour - enterTimeHour;
            enterMinute = myMinute - enterTimeMinute;
            Entered entered = new Entered("late", myHour + ":" + myMinute,false);
            if (!(enterHour > 7)) {
                databaseReference.child("attendance").child(today).child(userArrayList.get(x).getFkey()).child("entered").setValue(entered);
            } else {
                Toast.makeText(getActivity(), "Sorry but it is late", Toast.LENGTH_SHORT).show();
            }
            dialog.dismiss();
        }
    }

    public String getCurrentTime() {
        timeF = new SimpleDateFormat("HH:mm");
        time = timeF.format(Calendar.getInstance().getTime());

//        firebaseDateFormat = new SimpleDateFormat("dd_MM_yyyy");//2001.07.04
//        firebaseDate = firebaseDateFormat.format(Calendar.getInstance().getTime());

//        tvDate.setText(date);
        return time;
    }

    public void yesOrNo(final int position) {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        databaseReference.child(userArrayList.get(position).getFkey()).removeValue();
                        Toast.makeText(getActivity(), "Removed!", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        userAdapter.notifyDataSetChanged();
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

    private void initializeAdapterWithRecycler() {
        userAdapter.notifyDataSetChanged();
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(userAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutAnimation(animation);
        recyclerView.addItemDecoration(dividerItemDecoration);
    }

    private void initAll() {
        searchView = view.findViewById(R.id.searchView);
        userNotAdmin = view.findViewById(R.id.userNotAdmin);
        all = view.findViewById(R.id.all);
        progressBar = view.findViewById(R.id.ProgressBar);
        recyclerView = view.findViewById(R.id.recyclerView);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        databaseReference = FirebaseDatabase.getInstance().getReference();
        dividerItemDecoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
        userAdapter = new UserAdapter(getActivity(), userArrayList);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        int resId = R.anim.layout_animation_fall_down;
        animation = AnimationUtils.loadLayoutAnimation(getActivity(), resId);
        if(firebaseUser.getEmail().equals("parent@sdcl.kz")){
            all.setVisibility(View.GONE);
            userNotAdmin.setVisibility(View.VISIBLE);
        }
    }

    public void clickListener() {
        userAdapter.setOnClick(new ItemClickListener() {
            @Override
            public void onItemClick(View v, int pos) {
                if (!firebaseUser.getEmail().equals("check@sdcl.kz")) {
                    User item = userArrayList.get(pos);
                    Intent intent = new Intent(v.getContext(), Profile.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("user", item);
                    intent.putExtras(bundle);
                    v.getContext().startActivity(intent);
                }
            }

            @Override
            public void onItemLongClick(View v, int pos) {
                if (firebaseUser.getEmail().equals("admin@sdcl.kz") || firebaseUser.getEmail().equals("check@sdcl.kz")) {
                    initDialog(pos);
                }
            }
        });
    }

    public void forSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return false;
            }
        });
    }

    public void filter(String text) {
        userArrayList.clear();
        if (text.isEmpty()) {
            userArrayList.addAll(userArrayListCopy);
        } else {
            text = text.toLowerCase();
            for (User item : userArrayListCopy) {
                if (item.getCardNumber().toLowerCase().contains(text) || item.getInfo().toLowerCase().contains(text) ||
                        item.getPhoneNumber().toUpperCase().contains(text)) {
                    userArrayList.add(item);
                }
            }
        }
        userAdapter.notifyDataSetChanged();
    }
}
