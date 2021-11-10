package com.example.recognize.models.text;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Word implements Serializable {
    @SerializedName("boundingBox")
    private String boundingBox;
    @SerializedName("text")
    private String text;

    public Word(){}


    public String getBoundingBox() {
        return boundingBox;
    }

    public void setBoundingBox(String boundingBox) {
        this.boundingBox = boundingBox;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
