package com.example.recognize.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.recognize.R;
import com.example.recognize.utils.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class DashBoard extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private ImageView userDetailsBtn;
    private ImageView userGalleryBtn;
    private ImageView logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash);
        mAuth = FirebaseAuth.getInstance();

        userDetailsBtn = findViewById(R.id.user_details_btn);
        userDetailsBtn.setOnClickListener(v -> goToDetails());

        userGalleryBtn = findViewById(R.id.user_gallery_btn);
        userGalleryBtn.setOnClickListener(v -> goToGallery());

        logout = findViewById(R.id.logout_button);
        logout.setOnClickListener(v -> logoutDialog());
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
        System.out.print("Details");
    }


    public void goToGallery(){
        System.out.print("Gallery");
    }


}