package com.example.recognize.Activities;




import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import com.example.recognize.R;

public class Splash extends AppCompatActivity {

    private ImageView splashLogo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen);
        splashLogo = findViewById(R.id.splash_image);
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);
        splashLogo.setAnimation(animation);

        //After 3 seconds move from splash screen to the main screen of the UI.

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(Splash.this,Login.class );
                startActivity(intent);
                finish();
            }
        },2000);

    }

}