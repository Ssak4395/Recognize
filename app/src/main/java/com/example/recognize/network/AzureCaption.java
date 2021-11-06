package com.example.recognize.network;

import com.google.gson.annotations.SerializedName;

public class AzureCaption {
    @SerializedName("text")
    String text;
    @SerializedName("confidence")
    double confidence;

    AzureCaption(){}

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public double getConfidence() {
        return confidence;
    }

    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }
}
