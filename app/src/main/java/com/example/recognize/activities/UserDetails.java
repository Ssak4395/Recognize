package com.example.recognize.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;

import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;

import android.widget.ImageView;

import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import androidx.appcompat.app.AppCompatActivity;

import com.example.recognize.R;
import com.example.recognize.utils.Constants;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.Locale;


public class UserDetails extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private TextView userEmail;
    private TextView userFullName;
    private ImageView logout;
    private ImageView speaker;
    private TextToSpeech mTTS;
    private CollectionReference userCollectionRef;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    private String fullName;

    FirebaseDatabase firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);
        mAuth = FirebaseAuth.getInstance();
        userFullName = findViewById(R.id.user_name_text);
        userEmail = findViewById(R.id.user_email_text);
        userEmail.setText(currentUser.getEmail());

        speaker = findViewById(R.id.user_details_speaker);
        logout = findViewById(R.id.logout_button);
        logout.setOnClickListener(v -> logoutDialog());
        firebaseDatabase = FirebaseDatabase.getInstance();

        speaker.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onClick(View v) {
               speak("Your email address is "+ currentUser.getEmail()+" Your name is "+fullName);
            }
        });

        initTTS();
        getUserData();
    }

    /**
     * Get user name from firestore and set it to the Text Object
     */
    private void getUserData(){
        userCollectionRef = db.collection("users");
        DocumentReference userDoc = userCollectionRef.document(currentUser.getUid());
        userDoc.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.d("PLEASE","Listen failed: " + error);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    fullName = (String)snapshot.getData().get("firstName")+" "+(String)snapshot.getData().get("lastName");
                    userFullName.setText(fullName);
                } else {
                    Log.d("PLEASE","Current data: null");
                }
            }
        });
    }

    /**
     * Increases the volume of the device and calls the text to speech engine to speak text
     *
     * @param textToSpeech
     */
    private void speak(String textToSpeech) {
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
     * Logs the current user out
     */
    public void logout(){
        mAuth.signOut();
        Intent intent = new Intent(UserDetails.this, Login.class);
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



}