package com.example.recognize.network;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class AzureApiResponse implements Serializable {
    @SerializedName("description")
    private AzureDescription description;

    AzureApiResponse(){};

    public AzureDescription getDescription() {
        return description;
    }

    public void setDescription(AzureDescription description) {
        this.description = description;
    }
}


//class AzureDescription{
//    @SerializedName("captions")
//    ArrayList<AzureCaption> captions;
//    @SerializedName("tags")
//    ArrayList<String> tags;
//
//    AzureDescription(){};
//
//    public ArrayList<AzureCaption> getCaptions() {
//        return captions;
//    }
//
//    public void setCaptions(ArrayList<AzureCaption> captions) {
//        this.captions = captions;
//    }
//
//    public ArrayList<String> getTags() {
//        return tags;
//    }
//
//    public void setTags(ArrayList<String> tags) {
//        this.tags = tags;
//    }
//}

//class AzureCaption{
//    @SerializedName("text")
//    String text;
//    @SerializedName("confidence")
//    double confidence;
//
//    AzureCaption(){}
//
//    public String getText() {
//        return text;
//    }
//
//    public void setText(String text) {
//        this.text = text;
//    }
//
//    public double getConfidence() {
//        return confidence;
//    }
//
//    public void setConfidence(double confidence) {
//        this.confidence = confidence;
//    }
//}
