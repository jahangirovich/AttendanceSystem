package com.example.ainurbayanova.attendancesystem.modules;

public class Widget {
    private String nameOfImage;
    private String image;
    private String desc;

    public Widget(String nameOfImage, String image, String desc){
        this.nameOfImage = nameOfImage;
        this.image = image;
        this.desc = desc;
    }

    public String getNameOfImage() {
        return nameOfImage;
    }

    public String getImage() {
        return image;
    }

    public String getDesc() {
        return desc;
    }
}
