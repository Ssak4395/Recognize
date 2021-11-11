package com.example.recognize.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recognize.R;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

import com.example.recognize.models.User;

public class UsersListAdapter extends RecyclerView.Adapter<UsersListAdapter.UsersListViewHolder> {
    private static final String TAG = "UsersListAdapter";
    private ArrayList<User> usersList;
    private OnUserClick onUserClick;


    public UsersListAdapter(ArrayList<User> usersList, OnUserClick onUserClick) {
        this.usersList = usersList;
        this.onUserClick = onUserClick;
    }

    public interface OnUserClick {
        void onUserClick(String userId);
    }

    @NonNull
    @Override
    public UsersListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_list, parent, false);
        return new UsersListViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull UsersListViewHolder holder, int position) {
        User user = usersList.get(position);
        holder.bindItem(user);
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }


    class UsersListViewHolder extends RecyclerView.ViewHolder {

        private MaterialButton editBtn;
        private TextView firstName;
        private TextView lastName;
        private TextView email;


        public UsersListViewHolder(@NonNull View itemView) {
            super(itemView);
            editBtn = itemView.findViewById(R.id.edit_button);
            firstName = itemView.findViewById(R.id.user_first_name);
            lastName = itemView.findViewById(R.id.user_last_name);
            email = itemView.findViewById(R.id.user_email);

        }


        void bindItem(User user){
            Log.d(TAG, "bindItem: user" + user.getFirstName());
            firstName.setText(user.getFirstName());
            lastName.setText(user.getLastName());
            email.setText(user.getEmail());
            editBtn.setOnClickListener(v -> {
               onUserClick.onUserClick(user.getUid());
            });

        }
    }
}
