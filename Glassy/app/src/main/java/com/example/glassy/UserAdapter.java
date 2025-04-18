package com.example.glassy;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.viewholder> {
    Activity mainActivity;
    ArrayList<User> userArrayList;


    public UserAdapter(Activity mainActivity, ArrayList<User> userArrayList) {
        this.mainActivity = mainActivity;
        this.userArrayList = userArrayList;
    }

    @NonNull
    @Override
    public UserAdapter.viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mainActivity).inflate(R.layout.user_item,parent,false);
        return new viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapter.viewholder holder, int position) {

        User user = userArrayList.get(position);
        holder.username.setText(user.getUsername());
        holder.status.setText(user.getStatus());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mainActivity, Chat.class);
                intent.putExtra("userId",user.getUserId());
                intent.putExtra("username",user.getUsername());
                mainActivity.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userArrayList.size();
    }

    public class viewholder extends RecyclerView.ViewHolder {
        TextView username;
        TextView status;
        public viewholder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.usernameTextView);
            status = itemView.findViewById(R.id.statusTextView);
        }
    }
}
