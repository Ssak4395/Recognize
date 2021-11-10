package com.example.recognize.models.description;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Azure response object, description contains captions and tags.
 */
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
