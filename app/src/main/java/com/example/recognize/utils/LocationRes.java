package com.example.recognize.utils;

import android.location.Location;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class LocationRes implements Serializable {
    @SerializedName("top")
    private double top;
    @SerializedName("left")
    private double left;
    @SerializedName("width")
    private double width;
    @SerializedName("height")
    private double height;

    public LocationRes(){};

    public LocationRes(double top, double left, double width, double height) {
        this.top = top;
        this.left = left;
        this.width = width;
        this.height = height;
    }

    public double getTop() {
        return top;
    }

    public void setTop(double top) {
        this.top = top;
    }

    public double getLeft() {
        return left;
    }

    public void setLeft(double left) {
        this.left = left;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }
}
