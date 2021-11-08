package com.example.recognize.network;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface AzureManagerService {

    @POST("/vision/v3.2/describe")
    Call<AzureApiResponse> postPhoto(
            @Header("Content-Type") String contentType,
            @Header("Ocp-Apim-Subscription-Key") String key,
            @Body RequestBody image
    );

}
