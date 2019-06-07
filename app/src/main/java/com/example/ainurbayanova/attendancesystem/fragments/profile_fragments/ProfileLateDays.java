package com.example.ainurbayanova.attendancesystem.fragments.profile_fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.ainurbayanova.attendancesystem.R;
import com.example.ainurbayanova.attendancesystem.adapter.ProfileAttendanceAdapter;
import com.example.ainurbayanova.attendancesystem.modules.Attendance;
import com.example.ainurbayanova.attendancesystem.modules.Entered;
import com.example.ainurbayanova.attendancesystem.modules.User;
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
public class ProfileLateDays extends Fragment {


    public ProfileLateDays() {
        // Required empty public constructor
    }

    View view;
    DatabaseReference databaseReference;
    ProgressBar progressBar;
    LinearLayout all;
    TextView noLayout;
    ArrayList<Attendance> attendanceArrayList = new ArrayList<>();
    FirebaseUser firebaseUser;
    RecyclerView recyclerView;
    User my_user;
    Bundle bundle;
    ProfileAttendanceAdapter profileAttendanceAdapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profile_late_days, container, false);
        initWidgets();
        bundle = getArguments();
        if(bundle!=null){
            my_user = (User) bundle.getSerializable("user");
            uploadDatabase();
        }
        return view;
    }

    public void initWidgets() {
        databaseReference = FirebaseDatabase.getInstance().getReference();
        progressBar = view.findViewById(R.id.progressBarLoading);
        all = view.findViewById(R.id.all);
        noLayout = view.findViewById(R.id.noLayout);
        recyclerView = view.findViewById(R.id.attendanceRecyclerView);
        profileAttendanceAdapter = new ProfileAttendanceAdapter(getActivity(),attendanceArrayList);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    public void uploadDatabase() {

        databaseReference.child("attendance").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                attendanceArrayList.clear();
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    for (DataSnapshot dataKey : data.getChildren()) {
                        if (dataKey.getKey().equals(my_user.getFkey()) &&
                                dataKey.child("entered").child("action").getValue().toString().equals("late")) {
                            Entered entered = dataKey.child("entered").getValue(Entered.class);
                            Entered closed = dataKey.child("closed").getValue(Entered.class);
                            Attendance attendance = new Attendance(
                                    data.getKey(),
                                    "i1559550628490",
                                    my_user.getInfo(),
                                    my_user.getPhoto(),
                                    my_user.getSpinner(),
                                    entered,
                                    closed
                            );
                            attendanceArrayList.add(attendance);
                        }
                    }
                }
                if(attendanceArrayList.size() == 0){
                    all.setVisibility(View.GONE);
                    noLayout.setVisibility(View.VISIBLE);
                }
                progressBar.setVisibility(View.GONE);
                profileAttendanceAdapter.notifyDataSetChanged();
                initRecycler();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    public void initRecycler(){
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(profileAttendanceAdapter);
    }
}

