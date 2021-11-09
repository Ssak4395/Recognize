package com.example.recognize.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
    private Button toImageBtn;
    private Button toTextBtn;
    private ImageButton getLocationBtn;
    private ImageButton logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash);
        mAuth = FirebaseAuth.getInstance();
        toImageBtn = findViewById(R.id.to_img_rec);
        toTextBtn = findViewById(R.id.to_text_rec);
        getLocationBtn = findViewById(R.id.get_location);
        logout = findViewById(R.id.logout_btn);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Sign in success, update UI with the signed-in user's information
                Log.d("Successful!", "logout:success");
                Toast.makeText(DashBoard.this, "Successful logout!", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(DashBoard.this, Login.class);
                startActivity(intent);
            }
        });
        toImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Sign in success, update UI with the signed-in user's information
                Intent intent = new Intent(DashBoard.this, CameraHome.class);
                startActivity(intent);
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Sign in success, update UI with the signed-in user's information
                Log.d("Successful!", "logout:success");
                Toast.makeText(DashBoard.this, "Successful logout!", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(DashBoard.this, Login.class);
                startActivity(intent);
            }
        });

    }


}