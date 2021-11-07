package com.example.recognize.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitInstance {

    private static Retrofit instance = null;
    private static String BASE_URL = "https://recognizeelec5620.cognitiveservices.azure.com";


    public static Retrofit getInstance(){
        if(instance == null){
            instance = new Retrofit
                    .Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

        }
        return instance;
    }


}
