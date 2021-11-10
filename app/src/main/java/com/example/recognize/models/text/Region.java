package com.example.recognize.models.text;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class Region implements Serializable {
    @SerializedName("boundingBox")
    private String boundingBox;
    @SerializedName("lines")
    private ArrayList<Line> lines;

    public Region(){}

    public String getBoundingBox() {
        return boundingBox;
    }

    public void setBoundingBox(String boundingBox) {
        this.boundingBox = boundingBox;
    }

    public ArrayList<Line> getLines() {
        return lines;
    }

    public void setLines(ArrayList<Line> lines) {
        this.lines = lines;
    }
}
