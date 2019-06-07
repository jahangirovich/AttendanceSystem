package com.example.ainurbayanova.attendancesystem.modules;

import java.io.Serializable;

public class News implements Serializable{
    public String title,photo,text,date,fkey;

    public News(){

    }
    public News(String title,String photo,String text,String date,String fkey){
        this.title = title;
        this.photo = photo;
        this.text = text;
        this.date = date;
        this.fkey = fkey;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getFkey() {
        return fkey;
    }

    public void setFkey(String fkey) {
        this.fkey = fkey;
    }

    public String getDate() {
        return date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
