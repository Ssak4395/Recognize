package com.example.recognize.activities;

import android.content.Context;
import android.content.DialogInterface;

import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.ImageView;

import androidx.appcompat.app.AlertDialog;

import androidx.appcompat.app.AppCompatActivity;

import com.example.recognize.R;
import com.example.recognize.utils.Constants;
import com.example.recognize.utils.Utils;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Locale;


public class DashBoard extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private ImageView userDetailsBtn;
    private ImageView changeVoiceBtn;
    private ImageView logout;
    private TextToSpeech mTTS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash);
        mAuth = FirebaseAuth.getInstance();

        userDetailsBtn = findViewById(R.id.user_details_btn);
        userDetailsBtn.setOnClickListener(v -> goToDetails());

        changeVoiceBtn = findViewById(R.id.voice_change_btn);
        changeVoiceBtn.setOnClickListener(v -> changeVoice());

        logout = findViewById(R.id.logout_button);
        logout.setOnClickListener(v -> logoutDialog());
        initTTS();
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
        Intent intent = new Intent(DashBoard.this, Login.class);
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


    public void goToDetails(){
        Intent intent = new Intent(DashBoard.this, UserDetails.class);
        startActivity(intent);
    }


    public void changeVoice(){

        Utils.changePitch();
        speak("The speaker's voice has been changed.");
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
}