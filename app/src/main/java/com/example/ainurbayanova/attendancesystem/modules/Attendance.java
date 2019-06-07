package com.example.ainurbayanova.attendancesystem.modules;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;

@IgnoreExtraProperties
public class Attendance implements Serializable {
    String fKey;
    String info;
    String photo;
    String enterDate;
    String exitDate;
    String date;
    Entered entered;
    String group;
    Entered closed;
    int a = 10;

    public Attendance() {

    }

    public Attendance(String date, String fKey, String info, String photo, String enterDate, String exitDate){
        this.date = date;
        this.fKey = fKey;
        this.info = info;
        this.photo = photo;
        this.enterDate = enterDate;
        this.exitDate = exitDate;
    }

    public Attendance(String date, String fKey, String info, String photo, String group, Entered entered, Entered closed){
        this.date = date;
        this.fKey = fKey;
        this.info = info;
        this.photo = photo;
        this.entered = entered;
        this.closed = closed;
        this.group = group;
    }

    public Attendance(String fKey,Entered entered,Entered closed){
        this.fKey = fKey;
        this.entered = entered;
        this.closed = closed;
    }

    public Entered getClosed() {
        return closed;
    }

    public void setClosed(Entered closed) {
        this.closed = closed;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }


    public Entered getEntered() {
        return entered;
    }

    public void setEntered(Entered entered) {
        this.entered = entered;
    }

    //        attendance_list.add(new Attendance("123", "14.01.19", "attendance", "08:30", "17:00"));

    public Attendance(String date, String enterDate, String exitDate){
        this.date = date;
        this.enterDate = enterDate;
        this.exitDate = exitDate;
    }

    public Attendance(String date,String fKey, String enterDate, String exitDate){
        this.date = date;
        this.enterDate = enterDate;
        this.exitDate = exitDate;
        this.fKey = fKey;
    }

    public String getDate() {
        return date;
    }

    public String getfKey() {
        return fKey;
    }

    public void setfKey(String fKey) {
        this.fKey = fKey;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getEnterDate() {
        return enterDate;
    }

    public void setEnterDate(String enterDate) {
        this.enterDate = enterDate;
    }

    public String getExitDate() {
        return exitDate;
    }

    public void setExitDate(String exitDate) {
        this.exitDate = exitDate;
    }
}
