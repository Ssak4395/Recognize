package com.example.recognize.network;

import com.example.recognize.models.description.AzureApiResponse;
import com.example.recognize.models.read.ReadResponse;
import com.example.recognize.models.text.TextApiResponse;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface AzureManagerService {

    @POST("/vision/v3.2/describe")
    Call<AzureApiResponse> postPhoto(
            @Header("Content-Type") String contentType,
            @Header("Ocp-Apim-Subscription-Key") String key,
            @Body RequestBody image
    );


    @POST("/vision/v3.2/ocr")
    Call<TextApiResponse> postTextPhoto(
            @Header("Content-Type") String contentType,
            @Header("Ocp-Apim-Subscription-Key") String key,
            @Body RequestBody image
    );

    @POST("/vision/v3.2/read/analyze")
    Call<String> postTextPhotoForRead(
            @Header("Content-Type") String contentType,
            @Header("Ocp-Apim-Subscription-Key") String key,
            @Body RequestBody image
    );

    @GET("/vision/v3.2/read/analyzeResults/{operationId}")
    Call<ReadResponse> getResultForRead(
            @Path(value = "operationId") String operationId,
            @Header("Ocp-Apim-Subscription-Key") String key
    );


}
