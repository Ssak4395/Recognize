package com.example.recognize.models.read;

import com.example.recognize.models.text.Line;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class ReadResult implements Serializable {
    @SerializedName("page")
    private double page;
    @SerializedName("angle")
    private double angle;
    @SerializedName("width")
    private double width;
    @SerializedName("height")
    private double height;
    @SerializedName("unit")
    private String unit;
    @SerializedName("lines")
    private ArrayList<LineRead> lines;


    public ReadResult() {
    }

    public double getPage() {
        return page;
    }

    public void setPage(double page) {
        this.page = page;
    }

    public double getAngle() {
        return angle;
    }

    public void setAngle(double angle) {
        this.angle = angle;
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

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public ArrayList<LineRead> getLines() {
        return lines;
    }

    public void setLines(ArrayList<LineRead> lines) {
        this.lines = lines;
    }
}
