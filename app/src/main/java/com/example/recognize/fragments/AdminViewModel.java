package com.example.recognize.fragments;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.recognize.utils.Constants;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import com.example.recognize.models.User;

public class AdminViewModel extends ViewModel {
    private static final String TAG = "AdminViewModel";

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private MutableLiveData<User> currentUser = new MutableLiveData<>();
    private MutableLiveData<User> editingUser = new MutableLiveData<>();
    private MutableLiveData<ArrayList<User>> usersList = new MutableLiveData<>();

    public AdminViewModel() {
        // load user and users.
        getUsersFromFirebase();
    }

    public MutableLiveData<ArrayList<User>> getUsersList() {
        if (usersList.getValue() == null) {
            usersList.setValue(new ArrayList<>());
        }
        return usersList;
    }


    public void addUserToList(User user) {
        usersList.getValue().add(user);
        usersList.setValue(usersList.getValue());
    }

    public void getUsersFromFirebase() {
        Log.d(TAG, "getUsersFromFirebase");

        db.collection(Constants.USERS_COLLECTION).addSnapshotListener((value, error) -> {
            if (value != null) {
                Log.d(TAG, "getUsersFromFirebase: value empty" + value.isEmpty());
                ArrayList<User> tempList = new ArrayList<>();
                value.forEach(v -> {
                    User u = v.toObject(User.class);
                    Log.d(TAG, "user: " + u.toString());
//                    addUserToList(u);
                    tempList.add(u);
                });
                usersList.setValue(tempList);

            }
        });
    }


    public MutableLiveData<User> getEditingUser() {
        return editingUser;
    }

    public void loadUserFromFirebase(String userId) {
        db.collection(Constants.USERS_COLLECTION)
                .document(userId)
                .addSnapshotListener((value, error) -> {
                    if (value != null) {
                        User u = value.toObject(User.class);
                        if (u != null) {
                            editingUser.setValue(u);
                        }
                    }
                });

    }


    public void saveChanges(String userId, String firstName, String lastName, boolean isAdmin) {
        User updatedUser = editingUser.getValue();
        if (updatedUser != null) {
            updatedUser.setAdminUser(isAdmin);
            updatedUser.setFirstName(firstName);
            updatedUser.setLastName(lastName);

            db.collection(Constants.USERS_COLLECTION)
                    .document(userId)
                    .set(updatedUser)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Log.d(TAG, "onSuccess: user saved");
                            getUsersFromFirebase();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "onFailure: user not saved");
                }
            });
        }

    }


}
