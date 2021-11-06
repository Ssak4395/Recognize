package com.example.recognize.Activities;

import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.Size;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.recognize.R;
import com.example.recognize.databinding.ActivityCameraBinding;
import com.example.recognize.network.AzureApiService;
import com.google.android.gms.common.Feature;
import com.google.android.gms.common.util.IOUtils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Camera extends AppCompatActivity {

    private int REQUEST_CODE_PERMISSIONS = 101;
    private final String[] REQUIRED_PERMISSIONS = new String[]{"android.permission.CAMERA", "android.permission.WRITE_EXTERNAL_STORAGE"};
    private final String TAG = "CameraXBasic";
    private final String FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS";


    private ImageCapture imageCapture;
    private File outputDirectory;
    private ExecutorService cameraExecutor;
    private PreviewView viewFinder;

    private ImageView cameraCaptureButton;
    private ActivityCameraBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // use layout binding for safety
        binding = ActivityCameraBinding.inflate(this.getLayoutInflater());
        setContentView(binding.getRoot());
//        getSupportActionBar().hide();

        // request camera permission
        if (allPermissionsGranted()) {
            startCamera();
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }

        // setup capture button
        cameraCaptureButton = binding.cameraCaptureButton;
        cameraCaptureButton.setOnClickListener(v -> takePhoto());

//        viewFinder = findViewById(R.id.camera_view_finder);
        viewFinder = binding.cameraViewFinder;


        // get the output directory
        outputDirectory = getOutputDirectory();

        // camera executor thread
        cameraExecutor = Executors.newSingleThreadExecutor();

        //


    }




    private void takePhoto() {
        // needed in case imageCapture has changed.
        imageCapture = imageCapture;

        // set file details
        SimpleDateFormat df = new SimpleDateFormat(FILENAME_FORMAT, Locale.US);
        String date = df.format(new Date());
        File photoFile = new File(outputDirectory, df.format(System.currentTimeMillis()) + ".jpg");

        ImageCapture.OutputFileOptions outputOptions = new ImageCapture.OutputFileOptions.Builder(photoFile).build();


        imageCapture.takePicture(outputOptions, ContextCompat.getMainExecutor(this), new ImageCapture.OnImageSavedCallback() {
            @Override
            public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                Uri savedUri = Uri.fromFile(photoFile);

                AzureApiService azureApiService = new AzureApiService();
                BitmapFactory.decodeFile(photoFile.getAbsolutePath());

                azureApiService.getResultFromAPI(photoFile);
//
//                Image theImage = image.getImage();
//                assert theImage != null;
//
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        String res =
//
//
//                    }
//                }).start();


                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(Camera.this, "Image saved successfully :" + savedUri, Toast.LENGTH_SHORT).show();
                    }
                });
                // uri
                // msg
                // toast
            }

            @Override
            public void onError(@NonNull ImageCaptureException exception) {
                Log.e(TAG, "Photo capture failed", exception);

            }
        });

    }


//    public void sendImageForCloudProcessing() {
//        long startTime = System.currentTimeMillis();
//
//
//        AsyncTask.execute(new Runnable() {
//            @Override
//            public void run() {
//                Log.d("MainActivity", "Running async task to get details ");
//
//
//                try {
//                    Vision.Builder visionBuilder = new Vision.Builder(
//                            new NetHttpTransport(),
//                            new AndroidJsonFactory(),
//                            null
//                    );
//
//                    visionBuilder.setVisionRequestInitializer(new VisionRequestInitializer("AIzaSyAQHKLvpOSHY1AfjyhB39OLJkbMH7Oc-go"));
//
//                    Vision vision = visionBuilder.build();
//
//                    // Encode the photo as Cloud vision API expects input image to be a base64 string inside an Image object
//                    InputStream inputStream = getResources().openRawResource(R.raw.photo);
//                    byte[] photoData = IOUtils.toByteArray(inputStream);
//                    inputStream.close();
//
//                    // Create image object, add byte to it as a base64 string, pass array to the encodecontent.
//                    Image inputImage = new Image();
//                    inputImage.encodeContent(photoData);
//
//                    // Make a request
//                    Feature desiredFeature = new Feature();
//                    desiredFeature.setType("FACE_DETECTION");
//
//                    // compose an annotateimagerequest instance
//                    AnnotateImageRequest request = new AnnotateImageRequest();
//                    request.setImage(inputImage);
//                    request.setFeatures(Arrays.asList(desiredFeature));
//
//                    // request needs to belong to a batch annotate request object because the vision API is designed to process
//                    // multiple images at once.
//                    BatchAnnotateImagesRequest batchRequest = new BatchAnnotateImagesRequest();
//                    batchRequest.setRequests(Arrays.asList(request));
//
//
//                    BatchAnnotateImagesResponse batchResponse = vision.images().annotate(batchRequest).execute();
//
//                    long endTime = System.currentTimeMillis();
//                    Log.d("MainActivity", "start: " + startTime + "\nend: " + endTime);
//
//
//                    // use response
//                    List<FaceAnnotation> faces = batchResponse.getResponses().get(0).getFaceAnnotations();
//
//                    int numberOfFaces = faces.size();
//
//                    // get joy likelihood for each face
//                    String likelihoods = "";
//                    for (int i = 0; i < numberOfFaces; i++) {
//                        likelihoods += "\n It is " + faces.get(i).getJoyLikelihood() + " that face " + i + " is happy";
//                    }
//
//                    final String message = "This photo has " + numberOfFaces + "  faces" + likelihoods;
//                    Log.d("MainActivity", message);
//
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
//                        }
//                    });
//
//
//                } catch (IOException e) {
//                    Log.d("MainActivity", "Didn't work");
//                    e.printStackTrace();
//                }
//
//
//            }
//        });









        private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderListenableFuture = ProcessCameraProvider.getInstance(this);

        cameraProviderListenableFuture.addListener(new Runnable() {
            @Override
            public void run() {
                try {

                    ProcessCameraProvider cameraProvider = cameraProviderListenableFuture.get();

                    androidx.camera.core.Preview preview = new Preview.Builder().build();

                    preview.setSurfaceProvider(viewFinder.getSurfaceProvider());

                    imageCapture = new ImageCapture.Builder().build();


//                    ImageAnalysis imageAnalyzer = new ImageAnalysis.Builder().setTargetResolution(
//                            new Size(224, 225))
//                            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
//                            .build();
//
//                    imageAnalyzer.setAnalyzer(cameraExecutor, new ImageAnalysis.Analyzer() {
//                        @SuppressLint("UnsafeOptInUsageError")
//                        @Override
//                        public void analyze(@NonNull ImageProxy image) {
//                            int rotationDegrees = image.getImageInfo().getRotationDegrees();
//
//
//                            AzureApiService azureApiService = new AzureApiService();
//                            Image theImage = image.getImage();
//                            assert theImage != null;
//
//                            new Thread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    String res = azureApiService.getResultFromAPI(theImage);
//
//
//                                }
//                            }).start();




//                            @SuppressLint("UnsafeOptInUsageError") android.media.Image mediaImage = image.getImage();
//                            assert mediaImage != null;
//                            InputImage img = InputImage.fromMediaImage(mediaImage, image.getImageInfo().getRotationDegrees());
//
//
//
//
//
//                            objectDetector.process(img)
//                                    .addOnSuccessListener(
//                                            new OnSuccessListener<List<DetectedObject>>() {
//                                                @Override
//                                                public void onSuccess(@NonNull List<DetectedObject> detectedObjects) {
//                                                    //successful analysis
//                                                    for (DetectedObject dObj : detectedObjects) {
//                                                        for (DetectedObject.Label label : dObj.getLabels()) {
//                                                            Log.d(TAG, "success: " + label.getText());
//                                                        }
//
//                                                    }
//                                                    image.close();
//                                                }
//                                            }
//                                    ).addOnFailureListener(new OnFailureListener() {
//                                @Override
//                                public void onFailure(@NonNull Exception e) {
//                                    // failed
//                                    image.close();
//                                    Log.e(TAG, "Analyzing image failed");
//                                    e.printStackTrace();
//                                }
//                            });

//                        }
//                    });


                    CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;

                    try {
                        cameraProvider.unbindAll();

                        cameraProvider.bindToLifecycle(Camera.this, cameraSelector, preview, imageCapture);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }


                    //bindPreview(cameraProvider)


                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }, ContextCompat.getMainExecutor(this));

    }







    private boolean allPermissionsGranted() {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }



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




    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraExecutor.shutdown();
    }



}
