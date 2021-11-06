package com.example.recognize.network;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.util.Log;

import androidx.camera.core.ImageProxy;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.GenericSignatureFormatError;
import java.nio.ByteBuffer;
import java.nio.file.Files;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public class AzureApiService {

    private String BASEURL = "https://recognizeelec5620.cognitiveservices.azure.com/";
    private String key = "e49dcd7ae04a4d49b509fca64e085c84";
    private static final String TAG = "AzureApiService";

    // headers
    // content-type: application/octect-stream
    // Ocp-Apim-Subscription-Key : key



    interface AzureManagerService {
        @POST("/vision/v3.2/describe")
        Call<String> postPhoto(
                @Header("Content-Type") String contentType,
                @Header("Ocp-Apim-Subscription-Key") String key,
                @Body RequestBody image
        );
    }




    public String getResultFromAPI(File imageFile){
        byte[] bytes = new byte[(int) imageFile.length()];

        FileInputStream fis = null;
        try {
            fis = new FileInputStream(imageFile);
            fis.read(bytes);

            RequestBody requestBody = RequestBody.create(MediaType.parse("application/octet-stream"), bytes);
            Retrofit retrofit = new Retrofit.Builder().baseUrl("https://recognizeelec5620.cognitiveservices.azure.com").addConverterFactory(GsonConverterFactory.create()).build();
            AzureManagerService service = retrofit.create(AzureManagerService.class);



            Call<String> postPhotoCall = service.postPhoto("application/octet-stream",key, requestBody );

            postPhotoCall.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    Log.d(TAG, "successful response from azure ");
                    Log.d(TAG, "onResponse: "  + response);
                    Log.d(TAG, "onResponse: " + response.body());
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Log.d(TAG, "FAILED response from azure " + t);
                }
            });



        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


//        ByteBuffer buffer= image.getPlanes()[0].getBuffer();
//        byte[] bytes = new byte[buffer.capacity()];
//        buffer.get(bytes);

//        Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, null);








        return "test";
    }



}
