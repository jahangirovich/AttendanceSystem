package com.example.ainurbayanova.attendancesystem.modules;

public class moderator {
    public String image;
    public String name;
    public String desc;
    public int color;

    public moderator(String image, String name, String desc, int color) {
        this.image = image;
        this.name = name;
        this.desc = desc;
        this.color = color;
    }

    public String getImage() {

        return image;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

    public int getColor() {
        return color;
    }
}
