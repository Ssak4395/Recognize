package com.example.recognize.network;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Top level part of Azure JSON response
 */
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

