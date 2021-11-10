package com.example.recognize.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.recognize.R;
import com.google.android.material.button.MaterialButton;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AdminEditUser#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AdminEditUser extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final String TAG = "AdminEditUser";


    private String editUserId;
    private AdminViewModel model;
    private AutoCompleteTextView firstNameTxt;
    private AutoCompleteTextView lastNameTxt;
    private MaterialButton saveBtn;
    private RadioGroup adminRadioGroup;
    private RadioButton regularUserRadioButton;
    private RadioButton adminUserRadioButton;



    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AdminEditUser() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment adminEditUser.
     */
    // TODO: Rename and change types and number of parameters
    public static AdminEditUser newInstance(String param1, String param2) {
        AdminEditUser fragment = new AdminEditUser();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getActivity() != null) {
           model = new ViewModelProvider(getActivity()).get(AdminViewModel.class);
        }

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        editUserId = AdminEditUserArgs.fromBundle(getArguments()).getUserId();

        return inflater.inflate(R.layout.fragment_admin_edit_user, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        saveBtn = view.findViewById(R.id.save_edit_btn);
        firstNameTxt = view.findViewById(R.id.first_name_edit);
        lastNameTxt = view.findViewById(R.id.last_name_edit);
        adminRadioGroup = view.findViewById(R.id.edit_admin_user_radio_group);
        regularUserRadioButton = view.findViewById(R.id.radioUserOption);
        adminUserRadioButton = view.findViewById(R.id.radioAdminOption);


        model.loadUserFromFirebase(editUserId);

        final boolean[] set = {false};
        if(getActivity() != null){
            model.getEditingUser().observe(getActivity(), user -> {
                // check correct user
                if(user.getUid().equals(editUserId)){
                    firstNameTxt.setText(user.getFirstName());
                    lastNameTxt.setText(user.getLastName());

                    if(!set[0]){
                        boolean isAdmin = user.isAdminUser();
                        Log.d(TAG, "isAdmin? " + isAdmin);
                        if(isAdmin){
                            regularUserRadioButton.setChecked(false);
                            adminUserRadioButton.setChecked(true);
                        }else {
                            regularUserRadioButton.setChecked(true);
                            adminUserRadioButton.setChecked(false);
                        }
                        set[0] = true;
                    }
                }


            });
        }


        saveBtn.setOnClickListener(v ->  saveChanges());




    }


    public void saveChanges(){
        boolean isAdmin;
        if(adminUserRadioButton.isChecked()){
            isAdmin = true;
        } else {
            isAdmin = false;
        }

        model.saveChanges(editUserId, firstNameTxt.getText().toString(), lastNameTxt.getText().toString(), isAdmin);

        NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_admin);

        navController.navigateUp();
    }
}