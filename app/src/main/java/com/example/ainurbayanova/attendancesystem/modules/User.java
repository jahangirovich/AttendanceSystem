package com.example.ainurbayanova.attendancesystem.modules;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;

@IgnoreExtraProperties
public class User implements Serializable {
    String fkey;
    String info;
    String phoneNumber;
    String photo;
    String fPhotoName;
    String cardNumber;
    String spinner;

    public User() {

    }

    public User(String fkey, String info, String phoneNumber, String photo, String fPhotoName, String cardNumber,String spinner){
        this.fkey = fkey;
        this.info = info;
        this.phoneNumber = phoneNumber;
        this.photo = photo;
        this.fPhotoName = fPhotoName;
        this.phoneNumber = phoneNumber;
        this.cardNumber = cardNumber;
        this.spinner = spinner;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getFkey() {
        return fkey;
    }

    public void setFkey(String fkey) {
        this.fkey = fkey;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getfPhotoName() {
        return fPhotoName;
    }

    public void setfPhotoName(String fPhotoName) {
        this.fPhotoName = fPhotoName;
    }

    public String getSpinner() {
        return spinner;
    }

    public void setSpinner(String spinner) {
        this.spinner = spinner;
    }
}
