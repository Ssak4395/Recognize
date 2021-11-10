package com.example.recognize.models.read;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ReadResponse implements Serializable {
    @SerializedName("status")
    private String status;
    @SerializedName("createdDateTime")
    private String createdDateTime;
    @SerializedName("lastUpdatedDateTime")
    private String lastUpdatedDateTime;
    @SerializedName("analyzeResult")
    private AnalyzeResult analyzeResult;

    public ReadResponse() {
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreatedDateTime() {
        return createdDateTime;
    }

    public void setCreatedDateTime(String createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

    public String getLastUpdatedDateTime() {
        return lastUpdatedDateTime;
    }

    public void setLastUpdatedDateTime(String lastUpdatedDateTime) {
        this.lastUpdatedDateTime = lastUpdatedDateTime;
    }

    public AnalyzeResult getAnalyzeResult() {
        return analyzeResult;
    }

    public void setAnalyzeResult(AnalyzeResult analyzeResult) {
        this.analyzeResult = analyzeResult;
    }
}
