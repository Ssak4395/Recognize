package com.example.recognize.Activities;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class CameraHome extends AppCompatActivity {


    private int REQUEST_CODE_PERMISSIONS = 101;
    private final String[] REQUIRED_PERMISSIONS = new String[]{"android.permission.CAMERA",
            "android.permission.WRITE_EXTERNAL_STORAGE"};
    private final String TAG = "CameraXBasic";
    private final String FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS";


    private ImageCapture imageCapture;
    private File outputDirectory;
    private ExecutorService cameraExecutor;
    private PreviewView viewFinder;

    private ImageView cameraCaptureButton;
    private ActivityCameraHomeBinding binding;

    private TextToSpeech mTTS;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // use layout binding for safety
        binding = ActivityCameraHomeBinding.inflate(this.getLayoutInflater());
        setContentView(binding.getRoot());

        // request camera permission
        if (allPermissionsGranted()) {
            startCamera();
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }

        // setup capture button
        cameraCaptureButton = binding.cameraCaptureButton;
        cameraCaptureButton.setOnClickListener(v -> takePhoto());

        // camera view
        viewFinder = binding.cameraViewFinder;

        // get the output directory
        outputDirectory = getOutputDirectory();

        // camera executor thread
        cameraExecutor = Executors.newSingleThreadExecutor();

        initTTS();


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

                        getResultFromAzure(photoFile);

                        new Handler().post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(CameraHome.this, "Processing",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
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

            RequestBody requestBody = RequestBody.create(MediaType.parse("application/octet" +
                    "-stream"), bytes);
            Retrofit retrofit = new Retrofit.Builder().baseUrl("https://recognizeelec5620" +
                    ".cognitiveservices.azure.com").addConverterFactory(GsonConverterFactory.create()).build();
            AzureManagerService service = retrofit.create(AzureManagerService.class);

            String key = getResources().getString(R.string.azure_key);
            Call<AzureApiResponse> postPhotoCall = service.postPhoto("application/octet-stream",
                    key, requestBody);

            postPhotoCall.enqueue(new Callback<AzureApiResponse>() {
                @Override
                public void onResponse(Call<AzureApiResponse> call,
                                       Response<AzureApiResponse> response) {
                    Log.d(TAG, "successful response from azure ");
                    Log.d(TAG, "onResponse: " + response);
                    Log.d(TAG, "onResponse: " + response.body());
                    assert response.body() != null;
                    AzureApiResponse apiResponse = response.body();
                    AzureDescription azureDescription = apiResponse.getDescription();
                    AzureCaption topCaption = azureDescription.getCaptions().get(0);
                    if (topCaption != null) {
                        String description = topCaption.getText();
                        Toast.makeText(CameraHome.this, description, Toast.LENGTH_SHORT).show();
                        speak(description);
                    }
                    Log.d(TAG,
                            "onResponse: " + response.body().getDescription().getCaptions().get(0).getText());

                }

                @Override
                public void onFailure(Call<AzureApiResponse> call, Throwable t) {
                    Log.d(TAG, "FAILED response from azure " + t);

                }
            });

        } catch (FileNotFoundException e) {
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();
        }
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


    /** Increases the volume of the device and calls the text to speech engine to speak text
     * @param textToSpeech
     */
    private void speak(String textToSpeech) {
        AudioManager am = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        int amStreamMusicMaxVol = am.getStreamMaxVolume(am.STREAM_MUSIC);
        am.setStreamVolume(am.STREAM_MUSIC, amStreamMusicMaxVol, 0);
        mTTS.speak(textToSpeech, TextToSpeech.QUEUE_FLUSH, null);
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



}