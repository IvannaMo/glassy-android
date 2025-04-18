package com.example.glassy;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class MessageAdapter extends RecyclerView.Adapter {
    private Context context;
    private ArrayList<Message> messageArrayList;
    private int ITEM_SEND = 1;
    private int ITEM_RECIVE = 2;

    public MessageAdapter(Context context, ArrayList<Message> messageArrayList) {
        this.context = context;
        this.messageArrayList = messageArrayList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ITEM_SEND) {
            View view = LayoutInflater.from(context).inflate(R.layout.message_sender, parent, false);
            return new senderViewHolder(view);
        }
        else {
            View view = LayoutInflater.from(context).inflate(R.layout.message_receiver, parent, false);
            return new receiverViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = messageArrayList.get(position);
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                return false;
            }
        });

        if (holder.getClass() == senderViewHolder.class) {
            senderViewHolder viewHolder = (senderViewHolder)holder;
            viewHolder.messageTextView.setText(message.getMessage());
        }
        else {
            receiverViewHolder viewHolder = (receiverViewHolder)holder;
            viewHolder.messageTextView.setText(message.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return messageArrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messageArrayList.get(position);
        if (FirebaseAuth.getInstance().getCurrentUser().getUid().equals(message.getSenderUid())) {
            return ITEM_SEND;
        }
        else {
            return ITEM_RECIVE;
        }
    }

    class senderViewHolder extends RecyclerView.ViewHolder {
        TextView messageTextView;
        public senderViewHolder(@NonNull View itemView) {
            super(itemView);
            messageTextView = itemView.findViewById(R.id.senderMessageTextView);
        }
    }

    class receiverViewHolder extends RecyclerView.ViewHolder {
        TextView messageTextView;
        public receiverViewHolder(@NonNull View itemView) {
            super(itemView);
            messageTextView = itemView.findViewById(R.id.receiverMessageTextView);
        }
    }
}