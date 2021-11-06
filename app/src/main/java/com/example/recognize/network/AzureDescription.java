package com.example.recognize.network;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class AzureDescription {
    @SerializedName("captions")
    ArrayList<AzureCaption> captions;
    @SerializedName("tags")
    ArrayList<String> tags;

    AzureDescription(){};

    public ArrayList<AzureCaption> getCaptions() {
        return captions;
    }

    public void setCaptions(ArrayList<AzureCaption> captions) {
        this.captions = captions;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }
}
