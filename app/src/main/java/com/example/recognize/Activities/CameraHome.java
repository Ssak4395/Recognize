package com.example.recognize.Activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.Image;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkRequest;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.recognize.R;
import com.example.recognize.databinding.ActivityCameraHomeBinding;
import com.example.recognize.network.AzureApiResponse;
import com.example.recognize.network.AzureCaption;
import com.example.recognize.network.AzureDescription;
import com.example.recognize.network.AzureManagerService;
import com.example.recognize.network.RetrofitInstance;
import com.example.recognize.utils.Utils;
import com.example.recognize.utils.Constants;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import Models.User;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class CameraHome extends AppCompatActivity {


    private final int REQUEST_CODE_PERMISSIONS = 101;
    private final String[] REQUIRED_PERMISSIONS = new String[]{"android.permission.CAMERA",
            "android.permission.WRITE_EXTERNAL_STORAGE"};
    private final String TAG = "CameraXBasic";
    private final String FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS";


    private LocationManager locationManager;
    private LocationListener listener;
    private ImageCapture imageCapture;
    private File outputDirectory;
    private ExecutorService cameraExecutor;
    private PreviewView viewFinder;
    private ImageView button;
    private Location myLocation;

    private ImageView cameraCaptureButton;
    private ImageView logoutButton;
    private ImageView locationButton;
    private ImageView dashboardButton;
    private ActivityCameraHomeBinding binding;
    private FusedLocationProviderClient fusedLocationClient;
    private TextToSpeech mTTS;

    private boolean isNetworkConnected;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // use layout binding for safety
        binding = ActivityCameraHomeBinding.inflate(this.getLayoutInflater());
        setContentView(binding.getRoot());

        //
        mAuth = FirebaseAuth.getInstance();

        // request camera permission
        if (allPermissionsGranted()) {
            startCamera();
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }

        // setup capture button
        cameraCaptureButton = binding.cameraCaptureButton;
        cameraCaptureButton.setOnClickListener(v -> takePhoto());

        // setup logout button
        logoutButton = binding.logoutButton;
        logoutButton.setOnClickListener(v -> logoutDialog());

        // setup capture button
        dashboardButton = binding.dashboardButton;
        dashboardButton.setOnClickListener(v -> toDashBoard());

        //GPS Button
        button = findViewById(R.id.get_location);

        // setup logout button
        locationButton = binding.getLocation;

        // camera view
        viewFinder = binding.cameraViewFinder;

        // get the output directory
        outputDirectory = getOutputDirectory();

        // camera executor thread
        cameraExecutor = Executors.newSingleThreadExecutor();

        initTTS();
        initLocation();
        registerNetworkCallback();

        button.setOnClickListener(new View.OnClickListener() {
          @SuppressLint("MissingPermission")
          @Override
          public void onClick(View v) {
              Task<Location> lastLoc = fusedLocationClient.getLastLocation();
              lastLoc.addOnSuccessListener(new OnSuccessListener<Location>() {
                  @Override
                  public void onSuccess(Location location) {
                      speak(getReadableAddress(location));
                  }
              });
              lastLoc.addOnFailureListener(new OnFailureListener() {
                  @Override
                  public void onFailure(@NonNull Exception e) {
                      speak("Location retrieval has failed, please try again later or check location permissions.");
                  }
              });
          }
      });

    }
    /**
     * takePhoto is responsible for capturing the image currently displayed in the camera view,
     * saving
     * to a file for temporary storage initiates the azure analysis.
     */
    private void takePhoto() {
        // needed in case imageCapture has changed.
        imageCapture = imageCapture;

        // set file details
        SimpleDateFormat df = new SimpleDateFormat(FILENAME_FORMAT, Locale.US);
        String date = df.format(new Date());
        File photoFile = new File(outputDirectory, df.format(System.currentTimeMillis()) + ".jpg");

        ImageCapture.OutputFileOptions outputOptions =
                new ImageCapture.OutputFileOptions.Builder(photoFile).build();


        imageCapture.takePicture(outputOptions, ContextCompat.getMainExecutor(this),
                new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                        Uri savedUri = Uri.fromFile(photoFile);

                        BitmapFactory.decodeFile(photoFile.getAbsolutePath());

                        if (!isNetworkConnected) {
//                            String toSpeak = "Please check your network connection and try again";
                            speak(getString(R.string.check_connection));
                            Toast.makeText(CameraHome.this, R.string.check_connection,
                                    Toast.LENGTH_SHORT).show();

                        } else {
                            getResultFromAzure(photoFile);

                            new Handler().post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(CameraHome.this, R.string.processing_image,
                                            Toast.LENGTH_SHORT).show();
                                    speak(getResources().getString(R.string.processing_image));
                                }
                            });
                        }
                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        Log.e(TAG, "Photo capture failed", exception);

                    }
                });

    }

    /**
     * Uploads an image file to azure cognitive services api for analysis. Displays a toast with
     * result on success.
     *
     * @param imageFile an image file.
     */
    public void getResultFromAzure(File imageFile) {
        byte[] bytes = new byte[(int) imageFile.length()];

        FileInputStream fis = null;
        try {
            fis = new FileInputStream(imageFile);
            fis.read(bytes);

            // Get retrofit singleton
            Retrofit retrofit = RetrofitInstance.getInstance();
            // Use azure manager for JSON conversion.
            AzureManagerService service = retrofit.create(AzureManagerService.class);

            // Create request
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/octet" +
                    "-stream"), bytes);
            String key = getResources().getString(R.string.azure_key);
            Call<AzureApiResponse> postPhotoCall = service.postPhoto("application/octet-stream",
                    key, requestBody);

            postPhotoCall.enqueue(new Callback<AzureApiResponse>() {
                @Override
                public void onResponse(Call<AzureApiResponse> call,
                                       Response<AzureApiResponse> response) {
                    Log.d(TAG, "azure response: " + response);
                    if (response.isSuccessful()) {
                        Log.d(TAG, "SUCCESS response from azure");
                        if (response.body() != null) {
                            AzureApiResponse apiResponse = response.body();
                            AzureDescription azureDescription = apiResponse.getDescription();
                            AzureCaption topCaption = azureDescription.getCaptions().get(0);
                            if (topCaption != null) {
                                Log.d(TAG,
                                        "top description: " + response.body().getDescription().getCaptions().get(0).getText());
                                String description = topCaption.getText();
                                Toast.makeText(CameraHome.this, description, Toast.LENGTH_LONG).show();
                                speak(description);
                            }
                        }
                    } else {
                        Log.d(TAG, "FAILURE response from azure");
                        Toast.makeText(CameraHome.this, R.string.try_again, Toast.LENGTH_LONG).show();
                        speak(getResources().getString(R.string.try_again));
                    }
                }

                @Override
                public void onFailure(Call<AzureApiResponse> call, Throwable t) {
                    Log.d(TAG, "FAILED response from azure " + t);
                    Toast.makeText(CameraHome.this, R.string.try_again, Toast.LENGTH_LONG).show();
                    speak(getResources().getString(R.string.try_again));
                }
            });

        } catch (FileNotFoundException e) {
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Go to dashboard
     */
    private void toDashBoard(){
        Intent intent = new Intent(CameraHome.this, DashBoard.class);
        startActivity(intent);
    }

    /**
     * Start camera creates the camera provider and sets the surface
     */
    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderListenableFuture =
                ProcessCameraProvider.getInstance(this);

        cameraProviderListenableFuture.addListener(new Runnable() {
            @Override
            public void run() {
                try {

                    ProcessCameraProvider cameraProvider = cameraProviderListenableFuture.get();

                    androidx.camera.core.Preview preview = new Preview.Builder().build();

                    preview.setSurfaceProvider(viewFinder.getSurfaceProvider());

                    imageCapture = new ImageCapture.Builder().build();

                    CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;

                    try {
                        cameraProvider.unbindAll();

                        cameraProvider.bindToLifecycle(CameraHome.this, cameraSelector, preview,
                                imageCapture);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }


                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }, ContextCompat.getMainExecutor(this));

    }

    /**
     * checks if all permissions have been granted.
     *
     * @return boolean value of result.
     */
    private boolean allPermissionsGranted() {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    /**
     * After permissions are requested, the camera will start.
     *
     * @param requestCode  code of request
     * @param permissions  permissions array
     * @param grantResults permission results
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (allPermissionsGranted()) {
            startCamera();
        } else {
            Toast.makeText(this, "Permissions not granted by the user.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    /**
     * Gets the output directory for files or creates if one not already present.
     *
     * @return the file output directory
     */
    private File getOutputDirectory() {
        Optional<File> mediaDirOpt = Arrays.stream(getExternalMediaDirs()).findFirst();
        if (mediaDirOpt.isPresent()) {
            File mediaDir = mediaDirOpt.get();
            File f = new File(mediaDir, getResources().getString(R.string.app_name));
            boolean created = f.mkdirs();
            if (created) {
                return f;
            } else {
                return getFilesDir();
            }
        } else {
            return getFilesDir();
        }

    }

    /**
     * Destroys the camera view when finished
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraExecutor.shutdown();

        if (mTTS != null) {
            mTTS.stop();
            mTTS.shutdown();
        }

        super.onDestroy();
    }


    /**
     * Increases the volume of the device and calls the text to speech engine to speak text
     *
     * @param textToSpeech
     */
    public void speak(String textToSpeech) {
        AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int amStreamMusicMaxVol = am.getStreamMaxVolume(am.STREAM_MUSIC);
        am.setStreamVolume(am.STREAM_MUSIC, amStreamMusicMaxVol, 0);
        mTTS.setPitch(Constants.PITCH);
        mTTS.speak(textToSpeech, TextToSpeech.QUEUE_FLUSH, null);
    }

    /**
     * Initializes the text to speech system.
     */
    private void initTTS() {
        mTTS = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                int result = mTTS.setLanguage(Locale.ENGLISH);
                Log.d("TTS:", "the result was" + result);
                if (result == TextToSpeech.LANG_MISSING_DATA
                        || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e("TTS", "Language not supported");
                } else {

                }
            } else {
                Log.e("TTS", "Initialization failed");
            }
        },"com.google.android.tts");

    }


    /**
     * setup of a callback that changes the isNetworkConnected variable depending on
     * the connectivity status.
     */
    private void registerNetworkCallback() {
        try {
            ConnectivityManager connectivityManager =
                    (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkRequest.Builder builder = new NetworkRequest.Builder();

            connectivityManager.registerDefaultNetworkCallback(
                    new ConnectivityManager.NetworkCallback() {
                        @Override
                        public void onAvailable(@NonNull Network network) {
                            isNetworkConnected = true;
                        }

                        @Override
                        public void onLost(@NonNull Network network) {
                            isNetworkConnected = false;
                        }
                    });

            isNetworkConnected = false;

        } catch (Exception e) {
            isNetworkConnected = false;
        }
    }


    /**
     * Logs the current user out
     */
    public void logout() {
        mAuth.signOut();
        Intent intent = new Intent(CameraHome.this, Login.class);
        startActivity(intent);
    }

    /**
     * Checks the user intended to press the log out button
     */
    public void logoutDialog() {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder
                .setTitle("Logout")
                .setMessage("Are you sure you want to log out?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        logout();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                });
        alertBuilder.create().show();
    }


    /**
     * override on start to check if user is authenticated before proceeding.
     */
    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {

            Intent intent = new Intent(CameraHome.this, Login.class);
            startActivity(intent);
        } else {
            DocumentReference userRef = db.collection(Constants.USERS_COLLECTION).document(currentUser.getUid());
            userRef.addSnapshotListener((value, error) -> {
                if(value != null){
                    User user = value.toObject(User.class);
                    // determine if user is admin
                    if(user != null){
                        Log.d(TAG, "currentUSer: " + currentUser.toString());
                        boolean isAdmin = user.isAdminUser();
                        Intent intent;
                        if(isAdmin){
                            // go to admin dashboard
                            intent = new Intent(CameraHome.this, AdminDashboard.class);
                            startActivity(intent);
                        }

                    }
                } else {
                    Log.d(TAG, error.toString());
                }
            });




        }


    }


    private void initLocation()
    {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

    }

    private String getReadableAddress(Location location)
    {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());
        String address = "";
        String city = "";
        String state = "";
        String postalCode = "";
        String streetNumber  = "";

        try {
            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
             address = addresses.get(0).getThoroughfare(); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
             city = addresses.get(0).getLocality();
             state = addresses.get(0).getAdminArea();
             postalCode = addresses.get(0).getPostalCode();
             streetNumber = addresses.get(0).getFeatureName();
            System.out.println(addresses.get(0));

        } catch (IOException e) {
            e.printStackTrace();
        }


      return "You are currently located in " + streetNumber+ " " + address + " in the city of " + city + " located in the state of " + state + " The post code is " + postalCode;
    }

}