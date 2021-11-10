package com.example.recognize.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.recognize.R;
import com.example.recognize.utils.Constants;
import com.example.recognize.utils.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.example.recognize.models.User;


/**
 * The Login class is responsible for logging in existing users, with an option to register
 * if currently not a member.
 */
public class Login extends AppCompatActivity {
    private static final String TAG = "LoginActivity";

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    TextView register;
    Button login;
    EditText email;
    EditText password;
    TextView errorText1;
    TextView errorText2;
    TextView errorText3;
    TextView errorText4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        register = findViewById(R.id.register_button_login);
        login = findViewById(R.id.login_button);
        email = findViewById(R.id.editTextTextEmailAddress);
        password = findViewById(R.id.editTextTextPassword);
        errorText1 = findViewById(R.id.login_Error_text_1);//Empty input
        errorText2 = findViewById(R.id.login_Error_text_2);//No user found
        errorText3 = findViewById(R.id.login_Error_text_3);//No @ sign in email
        errorText4 = findViewById(R.id.login_Error_text_4);//No @ sign in email
        setupButtonListeners();
        setupOutsideClickListener();

    }



    /**
     * Helper function to setup button listeners for this activity
     */
    public void setupButtonListeners() {
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
                errorText1.setVisibility(View.INVISIBLE);
                errorText2.setVisibility(View.INVISIBLE);
                errorText3.setVisibility(View.INVISIBLE);
                errorText4.setVisibility(View.INVISIBLE);
                boolean validlogin = validLoginCredentials(email, password);
                if (validlogin) {
                    loginUser(email.getText().toString(), password.getText().toString());
                }else{
//                    errorText2.setVisibility(View.VISIBLE);
                }
            }
        });
    }


    /**
     * Used to login an existing user
     *
     * @param email    email of user
     * @param password password of user
     */
    public void loginUser(String email, String password) {
        FirebaseUser user = null;
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("Successful!", "signInWithEmail:success");
                            Toast.makeText(Login.this, "Successful login!", Toast.LENGTH_LONG).show();

                            String userId = mAuth.getCurrentUser().getUid();
                            DocumentReference userRef = db.collection(Constants.USERS_COLLECTION).document(userId);
                            userRef.addSnapshotListener((value, error) -> {
                                if(value != null){
                                    User currentUser = value.toObject(User.class);
                                    // determine if user is admin
                                    if(currentUser != null){
                                        Log.d(TAG, "currentUSer: " + currentUser.toString());
                                        boolean isAdmin = currentUser.isAdminUser();
                                        errorText2.setVisibility(View.VISIBLE);
                                        Intent intent;
                                        if(isAdmin){
                                            // go to admin dashboard
                                            intent = new Intent(Login.this, AdminDashboard.class);
                                        }else {
                                            // go to camera home
                                            intent = new Intent(Login.this, CameraHome.class);

                                        }
                                        startActivity(intent);
                                    }
                                } else {
                                    Log.d(TAG, error.toString());
                                }
                            });

//                            if (user != null) {
//                                errorText2.setVisibility(View.VISIBLE);
//                                Intent intent = new Intent(Login.this, CameraHome.class);
//                                startActivity(intent);
//                            }

                        } else {
                            errorText1.setVisibility(View.INVISIBLE);
                            errorText3.setVisibility(View.INVISIBLE);
                            errorText4.setVisibility(View.INVISIBLE);

                            errorText2.setVisibility(View.VISIBLE);
                            // If sign in fails, display a message to the user.
                            Log.w("Failed", "signInWithEmail:failure", task.getException());
                            Toast.makeText(Login.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    /**
     * Checks email/password text fields for valid input
     *
     * @param field1 email text field
     * @param field2 password text field
     * @return boolean depending on if the input is valid
     */
    public boolean validLoginCredentials(EditText field1, EditText field2) {

        // Lets firstly check none of the fields are empty.
        if (field1.getText().length() >= 1 && field2.getText().length() >= 1) {
            if(!field1.getText().toString().contains("@")){
                errorText3.setVisibility(View.VISIBLE);
                return false;
            }
            // Lets check if field1 is a valid email
            Pattern pattern = Pattern.compile("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}");
            Matcher mat = pattern.matcher(field1.getText());
            boolean isMatch = mat.matches();

            if (isMatch) {
                errorText4.setVisibility(View.INVISIBLE);

                return true;
            } else {
                errorText4.setVisibility(View.VISIBLE);
                return false;
            }
        }
        errorText1.setVisibility(View.VISIBLE);

        return false;
    }


    /**
     * Helper function to hide soft keyboard when clicking outside text field {@link Utils}
     */
    private void setupOutsideClickListener() {
        Utils.setUpCloseKeyboardOnOutsideClick(getWindow().getDecorView().getRootView(),
                Login.this);
    }


}