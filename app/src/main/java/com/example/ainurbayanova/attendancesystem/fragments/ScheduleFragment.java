package com.example.ainurbayanova.attendancesystem.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.ainurbayanova.attendancesystem.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * A simple {@link Fragment} subclass.
 */
public class ScheduleFragment extends Fragment {


    public ScheduleFragment() {
        // Required empty public constructor
    }
    ImageView schedule;
    View view;
    TextView noLayout;
    FirebaseUser firebaseUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_schedule, container, false);
        schedule = view.findViewById(R.id.schedule);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        noLayout = view.findViewById(R.id.noLayout);

        Glide.with(getActivity())
                .load("http://www.confessionsofahomeschooler.com/wp-content/uploads/2014/02/dailyschedule.jpg")
                .into(schedule);
        if(firebaseUser.getEmail().equals("admin@sdcl.kz") || firebaseUser.getEmail().equals("check@sdcl.kz")){
            noLayout.setVisibility(View.VISIBLE);
            schedule.setVisibility(View.GONE);
        }
        return view;
    }

}
