package com.example.recognize.models.read;

import java.io.Serializable;
import java.util.ArrayList;

public class LineRead implements Serializable {
    private ArrayList<Double> boundingBox;
    private String text;

    public LineRead() {
    }


    public ArrayList<Double> getBoundingBox() {
        return boundingBox;
    }

    public void setBoundingBox(ArrayList<Double> boundingBox) {
        this.boundingBox = boundingBox;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }


}
