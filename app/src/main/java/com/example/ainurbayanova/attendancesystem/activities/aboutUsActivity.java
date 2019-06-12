package com.example.ainurbayanova.attendancesystem.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.GridView;

import com.example.ainurbayanova.attendancesystem.R;
import com.example.ainurbayanova.attendancesystem.adapter.GridAdapter;
import com.example.ainurbayanova.attendancesystem.modules.About;

import java.util.ArrayList;

public class aboutUsActivity extends AppCompatActivity {
    Toolbar toolbar;
    GridView gridView;
    ArrayList<About> aboutUsArrayList = new ArrayList<>();
    GridAdapter gridAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        initAll();
        initGrid();
    }
    public void initAll(){
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("About Us");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        gridView = findViewById(R.id.gridView);
    }

    public void initGrid(){
        aboutUsArrayList.add(new About(R.drawable.zhigeragai,"Zhiger","Math"));
        aboutUsArrayList.add(new About(R.drawable.me,"Zhakhangir","Geometry"));
        aboutUsArrayList.add(new About(R.drawable.baurzhanagai,"Baurzhan","History"));
        gridAdapter = new GridAdapter(this,aboutUsArrayList);
        gridView.setAdapter(gridAdapter);
    }
}
