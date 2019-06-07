package com.example.ainurbayanova.attendancesystem.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

import com.example.ainurbayanova.attendancesystem.R;

public class AttendMe extends AppCompatActivity {
    EditText takeEdit;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);
        initWidgets();
        takeEdit.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)){
                    Log.i("info",takeEdit.getText().toString() + "");
                }
                return false;
            }
        });
    }

    public void initWidgets(){
        takeEdit = findViewById(R.id.editTextAttendance);
    }
}
