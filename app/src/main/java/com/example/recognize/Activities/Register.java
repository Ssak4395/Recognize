package com.example.recognize.Activities;

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

import Models.User;

/**
 * Register class is responsible for registering new users.
 */
public class Register extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private EditText email;  //Reference to user inputted text in UI
    private EditText password;
    private EditText firstName;
    private EditText lastName;

    private Button registerButton;
    private TextView errorText1;
    private TextView errorText2;
    private TextView errorText3;
    private TextView errorText4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // setup Firebase instances
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // setup views
        email = findViewById(R.id.email_address_register);
        password = findViewById(R.id.password_register);
        firstName = findViewById(R.id.first_name_register);
        lastName = findViewById(R.id.last_name_register);
        registerButton = findViewById(R.id.register_button);
        errorText1 = findViewById(R.id.register_error_text_1);//Empty input
        errorText2 = findViewById(R.id.register_error_text_2);//No @ sign in email
        errorText3 = findViewById(R.id.register_error_text_3);//User exists
        errorText4 = findViewById(R.id.register_error_text_4);//User exists

        setupButtonListeners();
        setupOutsideClickListener();

    }


    /**
     * Helper function to setup button listeners for this activity
     */
    public void setupButtonListeners() {

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                errorText1.setVisibility(View.INVISIBLE);
                errorText2.setVisibility(View.INVISIBLE);
                errorText3.setVisibility(View.INVISIBLE);
                errorText4.setVisibility(View.INVISIBLE);
                boolean validateRegister = validRegisterForm(email, password, firstName, lastName);
                if (validateRegister) {
                    errorText1.setVisibility(View.INVISIBLE);
                    errorText2.setVisibility(View.INVISIBLE);
                    errorText3.setVisibility(View.INVISIBLE);
                    errorText4.setVisibility(View.INVISIBLE);
                    createAccount(email.getText().toString(), password.getText().toString(),
                            firstName.getText().toString(), lastName.getText().toString());
                }
            }
        });
    }


    /**
     * Determines the validity of the registration form
     *
     * @param email     users email address
     * @param password  users password
     * @param firstName users first name
     * @param lastName  users last name
     * @return boolean depending on if the form has valid fields
     */
    public boolean validRegisterForm(EditText email, EditText password, EditText firstName,
                                     EditText lastName) {

        // Lets firstly check none of the fields are empty.
        if (email.getText().length() >= 1 && password.getText().length() >= 1 && firstName.getText().length() >= 1 && lastName.getText().length() >= 1) {
            if (!email.getText().toString().contains("@")) {
                errorText2.setVisibility(View.VISIBLE);
                return false;
            }
            // Lets check if field1 is a valid email
            Pattern pattern = Pattern.compile("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}");
            Matcher mat = pattern.matcher(email.getText());
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

    private void createAccount(String email, String password, String firstName, String lastName) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            String uid = mAuth.getCurrentUser().getUid();


                            User userToAdd = new User();
                            userToAdd.setEmail(email);
                            userToAdd.setFirstName(firstName);
                            userToAdd.setLastName(lastName);
                            userToAdd.setAdminUser(false);
                            userToAdd.setUid(uid);
                            addUserToFirestore(userToAdd);

                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(Register.this, "REGISTRATION " +
                                    "SUCCESSFUL", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(Register.this, CameraHome.class);
                            startActivity(intent);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.d("EXCEPTION", task.getException().toString());
                            Toast.makeText(Register.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    /**
     * Adds a user to Firebase Firestore 'users' collection.
     *
     * @param user the new User {@link User} to add
     */
    private void addUserToFirestore(User user) {
        DocumentReference documentReference =
                db.collection(Constants.USERS_COLLECTION).document(user.getUid());

        documentReference.set(user).addOnSuccessListener(unused -> Log.d(TAG, "onSuccess: user " +
                "success")).addOnFailureListener(e -> Log.d(TAG, "onFailure: user not created in " +
                "firestore"));


    }


    /**
     * Helper function to hide soft keyboard when clicking outside text field {@link Utils}
     */
    private void setupOutsideClickListener() {
        Utils.setUpCloseKeyboardOnOutsideClick(getWindow().getDecorView().getRootView(),
                Register.this);
    }

}