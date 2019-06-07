package com.example.ainurbayanova.attendancesystem.modules;

public class Entered {
    public String action,time;
    public boolean notified;
    public Entered(String action,String time,boolean notified){
        this.action = action;
        this.time = time;
        this.notified = notified;
    }

    public Entered(){

    }

    public boolean getNotified() {
        return notified;
    }

    public void setNotified(boolean notified) {
        this.notified = notified;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
