package com.example.ainurbayanova.attendancesystem.interfaces;

import com.example.ainurbayanova.attendancesystem.modules.User;

public interface UserFirebaseInterface {
    interface model{
        interface OnFinishedListener{
            void onFinished(User user);
        }
        void onResponse(OnFinishedListener onFinishedListener, String username);
    }
    interface Presenter{
        void requestFromFirbase(String username);
    }
    interface View{
        void showProgress();
        void hideProgress();
        void setData(User user);
    }
}
