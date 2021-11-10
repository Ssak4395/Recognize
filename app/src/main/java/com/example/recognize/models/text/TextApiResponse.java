package com.example.recognize.models.text;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class TextApiResponse implements Serializable {

    @SerializedName("language")
    private String language;
    @SerializedName("textAngle")
    private double textAngle;
    @SerializedName("orientation")
    private String orientation;
    @SerializedName("regions")
    private ArrayList<Region> regions;

    public TextApiResponse(){}

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public double getTextAngle() {
        return textAngle;
    }

    public void setTextAngle(double textAngle) {
        this.textAngle = textAngle;
    }

    public String getOrientation() {
        return orientation;
    }

    public void setOrientation(String orientation) {
        this.orientation = orientation;
    }

    public ArrayList<Region> getRegions() {
        return regions;
    }

    public void setRegions(ArrayList<Region> regions) {
        this.regions = regions;
    }

    @Override
    public String toString() {
        return "TextApiResponse{" +
                "language='" + language + '\'' +
                ", textAngle=" + textAngle +
                ", orientation='" + orientation + '\'' +
                ", regions=" + regions +
                '}';
    }
}
