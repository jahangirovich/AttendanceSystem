package com.example.ainurbayanova.attendancesystem.modules;

public class Image {
    public String image_name;
    public String description;
    public String place;
    public String getImage_name() {
        return image_name;
    }

    public String getDescription() {
        return description;
    }

    public String getPlace() {
        return place;
    }

    public Image(String image_name, String description, String place){
        this.image_name = image_name;
        this.description = description;
        this.place = place;
    }
}
