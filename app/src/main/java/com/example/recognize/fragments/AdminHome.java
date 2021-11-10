package com.example.recognize.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recognize.R;
import com.example.recognize.adapters.UsersListAdapter;

import java.util.ArrayList;

import com.example.recognize.models.User;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AdminHome#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AdminHome extends Fragment {
    private static final String TAG = "AdninHomeFragment";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    private RecyclerView usersRecyclerView;
    private ArrayList<User> userList;
    private UsersListAdapter usersListAdapter;
    private AdminViewModel model;


    public AdminHome() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AdminHome.
     */
    // TODO: Rename and change types and number of parameters
    public static AdminHome newInstance(String param1, String param2) {
        AdminHome fragment = new AdminHome();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (getActivity() != null) {
            model = new ViewModelProvider(getActivity()).get(AdminViewModel.class);
        }
        return inflater.inflate(R.layout.fragment_admin_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        usersRecyclerView = view.findViewById(R.id.users_recycler_view);

        setupRecyclerView();
        setupUsersObserver();


    }

    /**
     * Click listener for a user in the list.
     */
    UsersListAdapter.OnUserClick onUserClickListener = userId -> {
        if (getActivity() != null) {
            NavController navController = Navigation.findNavController(getActivity(),
                    R.id.nav_host_fragment_admin);
            Bundle bundle = new Bundle();
            bundle.putString("userId", userId);
            navController.navigate(R.id.action_adminHome_to_adminEditUser, bundle);
        }
    };

    public void setupRecyclerView() {
        if (userList == null) {
            userList = new ArrayList<>();
        }

        usersRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        usersListAdapter = new UsersListAdapter(userList, onUserClickListener);
        usersRecyclerView.setAdapter(usersListAdapter);


    }


    private void setupUsersObserver() {

        model.getUsersList().observe(getActivity(), users -> {
            Log.d(TAG, "setupUsersObserver: ");
            userList.clear();
            userList.addAll(users);
            usersListAdapter.notifyDataSetChanged();
        });

    }


}