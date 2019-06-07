package com.example.ainurbayanova.attendancesystem.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ainurbayanova.attendancesystem.R;
import com.example.ainurbayanova.attendancesystem.adapter.NoteAdapter;
import com.example.ainurbayanova.attendancesystem.modules.Notes;

import java.util.ArrayList;


public class NoteFragment extends Fragment {
    RecyclerView recyclerView;
    ArrayList<Notes> notes = new ArrayList<>();
    NoteAdapter adapter;
    public NoteFragment() {
        // Required empty public constructor
    }
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_note, container, false);
        initRecycler();
        return view;
    }

    private void initRecycler() {
        initializeItems();
        recyclerView = view.findViewById(R.id.recyclerView);
        adapter = new NoteAdapter(getActivity(),notes);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);
    }

    private void initializeItems() {
        notes.add(new Notes("7 navikov visokoeffectivnix","About improving yourself"));
        notes.add(new Notes("Volokolomskoe wosse","About honor"));
        notes.add(new Notes("Podsoznanie mozhet vse","About podsoznanie"));
        notes.add(new Notes("Podsoznanie mozhet vse","About podsoznanie"));
    }

}
