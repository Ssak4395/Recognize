package com.example.recognize.models.read;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class AnalyzeResult {
    @SerializedName("version")
    private String version;
    @SerializedName("readResults")
    private ArrayList<ReadResult> readResults;

    public AnalyzeResult() {
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public ArrayList<ReadResult> getReadResults() {
        return readResults;
    }

    public void setReadResults(ArrayList<ReadResult> readResults) {
        this.readResults = readResults;
    }
}
