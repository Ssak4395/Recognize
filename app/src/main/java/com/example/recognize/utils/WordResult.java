package com.example.recognize.utils;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class WordResult implements Serializable {
    @SerializedName("location")
    private LocationRes location;
    @SerializedName("words")
    private String words;

    public WordResult(){}

    public LocationRes getLocation() {
        return location;
    }

    public void setLocation(LocationRes location) {
        this.location = location;
    }

    public String getWords() {
        return words;
    }

    public void setWords(String words) {
        this.words = words;
    }
}
