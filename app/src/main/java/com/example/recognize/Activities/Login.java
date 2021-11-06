package com.example.recognize.Activities;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.recognize.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class Login extends AppCompatActivity {

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    TextView register;
    Button login;
    EditText email;
    EditText password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        register = findViewById(R.id.register_button_login);
        login = findViewById(R.id.login_button);
        email = findViewById(R.id.editTextTextEmailAddress);
        password = findViewById(R.id.editTextTextPassword);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Register.class);
                startActivity(intent);
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean validlogin = validLoginCredentials(email,password);
                if(validlogin)
                {
                    loginUser(email.getText().toString(),password.getText().toString());

                }
            }
        });

    }


    /**
     * This is self explanatory
     * @param email
     * @param password
     */
    public void loginUser(String email, String password)
    {
        FirebaseUser user = null;
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("Successful!", "signInWithEmail:success");
                            Toast.makeText(Login.this,"Successful login!!",Toast.LENGTH_LONG).show();
                            FirebaseUser user = mAuth.getCurrentUser();

                            if(user != null)
                            {
                                Intent intent = new Intent(Login.this,Camera.class );
                                startActivity(intent);
                            }

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("Failed", "signInWithEmail:failure", task.getException());
                            Toast.makeText(Login.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    /**
     *
     * @param field1
     * @param field2
     * @return
     */
    public boolean validLoginCredentials(EditText field1, EditText field2)
    {
        // Lets firstly check none of the fields are empty.
        if(field1.getText().length() > 1  && field2.getText().length() > 1)
        {
            // Lets check if field1 is a valid email

            Pattern pattern = Pattern.compile("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}");
            Matcher mat = pattern.matcher(field1.getText());
            boolean isMatch = mat.matches();

            if(isMatch)
            {
                return true;
            }
            else
            {
                return false;
            }
        }

        return false;
    }


}