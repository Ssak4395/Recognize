package com.example.recognize.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.location.Address;
import android.location.Geocoder;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.recognize.R;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Locale;

import Utilities.GPSHandler;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class CameraHome extends AppCompatActivity {

    private Camera mCamera; // instance of the device camera
    private CameraView mPreview; //Instance of the camera preview frame
    private ImageView cameraBTN; // Button to capture photo in camera view
    private ImageView dictateLocationBtn;
    private LocationRequest locationRequest;
    private TextToSpeech mTTS;
    private String myLocation;




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_home);

        mCamera = getCameraInstance();
        cameraBTN = findViewById(R.id.cameraBTN);
        mPreview = new CameraView(this, mCamera);
        FrameLayout preview = (FrameLayout) findViewById(R.id.cameraView);
        preview.addView(mPreview);


        // setups a location request object
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(100000000); //Poll location once only.
        locationRequest.setFastestInterval(1000000000);

        startLocation();
        initTTS();

        cameraBTN = findViewById(R.id.cameraBTN);
        // Create our Preview view and set it as the content of our activity.
        cameraBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCamera.takePicture(null, null, mPicture);
            }
        });

        dictateLocationBtn = findViewById(R.id.dictation_button);
        dictateLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              speak(myLocation);
            }
        });


    }





    /**
     *
     * @return Camera instance, ready to be used by class.
     */
    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }

    /**
     * Picture callback inner anonymous method is used to retrieve picture data after the user
     * has taken a photo.
     */
    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {


            Toast.makeText(CameraHome.this,"A Picture has been successfully taken but not stored(for now)",Toast.LENGTH_LONG).show();



            // Get the image taken by the user.
            Bitmap bitmap = BitmapFactory.decodeByteArray(data,0,data.length);
            // Init Intent
            Intent intent = new Intent(CameraHome.this,Preview.class);
            //Store image in a "Extra"
            Matrix matrix = new Matrix(); // Get matrix to rotate image

            matrix.postRotate(90); // Rotate the image by 90 degrees

            Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight(), true); // scale the bitmap, and set it to rotate

            Bitmap rotatedBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true); // Create new rotated bitmap

            intent.putExtra("uri",insertImage(getContentResolver(),rotatedBitmap,"Image"+System.currentTimeMillis(),"This is a image taken using the camera")); // Put the bitmap in the intent
            //Start Intent
            startActivity(intent);
            //Make camera usable for next session.
            camera.startPreview();


        }



    };


    /**
     *
     * @param cr Current Content Resolver of the device
     * @param source Source Bitmap Image.
     * @param title Title of the bitmap
     * @param description Description of the Bitmap
     * @return Uri pointing to the file.
     */
    public static final Uri insertImage(ContentResolver cr,
                                        Bitmap source,
                                        String title,
                                        String description) {

        ContentValues values = new ContentValues();
        // Put variables to persist in device
        values.put(MediaStore.Images.Media.TITLE, title);
        values.put(MediaStore.Images.Media.DISPLAY_NAME, title);
        values.put(MediaStore.Images.Media.DESCRIPTION, description);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        // Add the date meta data to ensure the image is added at the front of the gallery
        values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis());
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());

        //set URI to NUll
        Uri url = null;
        String stringUrl = null;    /* value to be returned */

        try {
            // try to insert image
            url = cr.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

            if (source != null) {
                OutputStream imageOut = cr.openOutputStream(url);
                try {
                    //Compress images to 50 per cent so they are smaller and much easier to upload.
                    source.compress(Bitmap.CompressFormat.JPEG, 50, imageOut);
                } finally {
                    imageOut.close();
                }

            } else {
                cr.delete(url, null, null);
                url = null;
            }
        } catch (Exception e) {
            if (url != null) {
                cr.delete(url, null, null);
                url = null;
            }
        }

        if (url != null) {
            stringUrl = url.toString();
        }

        return url;
    }


    /**
     * Turns on the GPS
     */

    private void turnOnGPS() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(getApplicationContext())
                .checkLocationSettings(builder.build());

        result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {

                try {
                    LocationSettingsResponse response = task.getResult(ApiException.class);
                    Toast.makeText(CameraHome.this, "GPS is already tured on", Toast.LENGTH_SHORT).show();

                } catch (ApiException e) {

                    switch (e.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:

                            try {
                                ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                                resolvableApiException.startResolutionForResult(CameraHome.this, 2);
                            } catch (IntentSender.SendIntentException ex) {
                                ex.printStackTrace();
                            }
                            break;

                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            //Device does not have location
                            break;
                    }
                }
            }
        });

    }


    /**
     * Uses android geocoding to get the full location via lat and long coords
     * @param lat
     * @param lon
     * @return
     * @throws IOException
     */

    //We need to call this on a separate thread, I am too lazy to figure this out on a saturday night will solve soon sorry.
    private String getFullAddress(double lat, double lon) throws IOException {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());

        addresses = geocoder.getFromLocation(lat, lon, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

        String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
        String city = addresses.get(0).getLocality();
        String state = addresses.get(0).getAdminArea();
        String country = addresses.get(0).getCountryName();
        String postalCode = addresses.get(0).getPostalCode();
        String knownName = addresses.get(0).getFeatureName();

        return "Your are currently located at " + address +
                ", In the city" + city +
                "Located in the state of " + state  +
                " The current post code is " + postalCode;
    }


    /**
     * Increases the volume of the device and calls the text to speech engine to speak text
     * @param textToSpeech
     */
    private void speak(String textToSpeech) {
        AudioManager am = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        int amStreamMusicMaxVol = am.getStreamMaxVolume(am.STREAM_MUSIC);
        am.setStreamVolume(am.STREAM_MUSIC, amStreamMusicMaxVol, 0);
        mTTS.speak(textToSpeech, TextToSpeech.QUEUE_FLUSH, null);
    }


    /**
     * When the view changes the text to speech is automatically stopped.
     */
    @Override
    protected void onDestroy() {
        if (mTTS != null) {
            mTTS.stop();
            mTTS.shutdown();
        }

        super.onDestroy();
    }

    /**
     * Initializes the text to speech system.
     */
    private void initTTS()
    {
        mTTS = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                int result = mTTS.setLanguage(Locale.ENGLISH);
                Log.d("TTS:","the result was" + result);


                if (result == TextToSpeech.LANG_MISSING_DATA
                        || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e("TTS", "Language not supported");
                } else {

                }
            } else {
                Log.e("TTS", "Initialization failed");
            }
        });

    }

    /**
     * Uses the device GPS to start listening of location
     */
    @SuppressLint("MissingPermission")
    private void startLocation()
    {
        if (GPSHandler.isGPSEnablesd(CameraHome.this)) {

            LocationServices.getFusedLocationProviderClient(CameraHome.this).requestLocationUpdates(locationRequest, new LocationCallback() {
                @Override
                public void onLocationResult(@NonNull LocationResult locationResult) {
                    super.onLocationResult(locationResult);

                    LocationServices.getFusedLocationProviderClient(CameraHome.this).removeLocationUpdates(this);

                    if(locationResult != null &&  locationResult.getLocations().size() > 0)
                    {
                        int idx = locationResult.getLocations().size() -1;

                        try {
                            myLocation= getFullAddress(locationResult.getLastLocation().getLatitude(),locationResult.getLastLocation().getLongitude());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                }
            }, Looper.getMainLooper());
        }
        else
        {
            turnOnGPS();
        }
    }
}