package com.example.ainurbayanova.attendancesystem.interfaces;

import com.example.ainurbayanova.attendancesystem.modules.Attendance;

import java.util.ArrayList;

public interface UserStoriesInterface {
    interface Model{
        interface OnFinishedListener{
            void onFinished(ArrayList<Attendance> challenges);
        }
        void onResponse(UserStoriesInterface.Model.OnFinishedListener onFinishedListener, String fKey);
    }
    interface Presenter{
        void requestFromFirebase(String fKey);
    }
    interface View{
        void showProgressBar();
        void hideProgressBar();
        void setDatasToRecylerView(ArrayList<Attendance> challenges);
    }
}
