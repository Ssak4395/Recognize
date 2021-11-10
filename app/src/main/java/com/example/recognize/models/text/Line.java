package com.example.recognize.models.text;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class Line implements Serializable {
    @SerializedName("boundingBox")
    private String boundingBox;
    @SerializedName("words")
    private ArrayList<Word> words;

    @SerializedName("text")
    private String text;


    public Line(){}

    public String getBoundingBox() {
        return boundingBox;
    }

    public void setBoundingBox(String boundingBox) {
        this.boundingBox = boundingBox;
    }

    public ArrayList<Word> getWords() {
        return words;
    }

    public void setWords(ArrayList<Word> words) {
        this.words = words;
    }


    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
