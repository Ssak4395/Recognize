package com.example.recognize.models.description;

import com.google.gson.annotations.SerializedName;

/**
 * Azure response object. A caption is a full sentence description of the image.
 */
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
