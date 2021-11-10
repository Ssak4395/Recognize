package com.example.recognize.utils;

import com.google.gson.annotations.SerializedName;

import org.w3c.dom.Text;

import java.io.Serializable;
import java.util.ArrayList;

public class TextRecognitionResponse implements Serializable {

    @SerializedName("words_result")
    private ArrayList<WordResult> wordResults;

    public TextRecognitionResponse(){};

    public TextRecognitionResponse(ArrayList<WordResult> wordResults) {
        this.wordResults = wordResults;
    }

    public ArrayList<WordResult> getWordResults() {
        return wordResults;
    }

    public void setWordResults(ArrayList<WordResult> wordResults) {
        this.wordResults = wordResults;
    }
}
