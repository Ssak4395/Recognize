package com.example.recognize.Activities;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.recognize.R;

import java.io.IOException;


/**
 * Activity that is used to preview the user image.
 */
public class Preview extends AppCompatActivity {

    private  ImageView view;  // Image View to be displayed.
    private  Button cancel; // Cancel button
    private Context context = this; //Reference to this context


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);
        view = findViewById(R.id.check); // Set The Image View


        final Uri uri = (Uri) getIntent().getExtras().get("uri"); // Get the URI from the cameraActivity Class
        Bitmap bitmap = null; //Set Bitmap to Null
        try {
            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri); //Try finding the image.
        } catch (IOException e) {
            e.printStackTrace();
        }
        view.setImageBitmap(bitmap); // Set imageView as the image the user has taken




        cancel = findViewById(R.id.Cancel); //Load cancel button


        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context,CameraHome.class);
                startActivity(intent); // Return to home activity on Button Click.

            }
        });



    }


}