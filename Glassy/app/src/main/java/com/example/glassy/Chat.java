package com.example.glassy;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;

public class Chat extends AppCompatActivity {
    private String receiverUid;
    private String receiverUsername;
    private String senderUid;
    private String receiverRoom;
    private String senderRoom;
    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private TextView receiverUsernameTextView;
    private RecyclerView messagesRecyclerView;
    private EditText chatMessageEditText;
    private Button sendMessageButton;
    private ArrayList<Message> messageArrayList;
    private MessageAdapter messageAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance("https://aline-glassy-default-rtdb.europe-west1.firebasedatabase.app/");

        receiverUid = getIntent().getStringExtra("userId");
        receiverUsername = getIntent().getStringExtra("username");

        receiverUsernameTextView = findViewById(R.id.receiverUsernameTextView);
        receiverUsernameTextView.setText(receiverUsername);

        messagesRecyclerView = findViewById(R.id.messagesRecyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        messagesRecyclerView.setLayoutManager(linearLayoutManager);

        messageArrayList = new ArrayList<>();
        messageAdapter = new MessageAdapter(Chat.this, messageArrayList);
        messagesRecyclerView.setAdapter(messageAdapter);

        chatMessageEditText = findViewById(R.id.chatMessageEditText);
        chatMessageEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        senderUid =  auth.getUid();
        senderRoom = senderUid + receiverUid;
        receiverRoom = receiverUid + senderUid;

        DatabaseReference  chatReference = database.getReference().child("chats").child(senderRoom).child("messages");
        chatReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messageArrayList.clear();
                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                    Message message = dataSnapshot.getValue(Message.class);
                    messageArrayList.add(message);
                }
                messageAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        sendMessageButton = findViewById(R.id.sendMessageButton);
        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String chatMessage = chatMessageEditText.getText().toString();
                if (chatMessage.isEmpty()){
                    Toast.makeText(Chat.this, "Enter The Message First", Toast.LENGTH_SHORT).show();
                    return;
                }
                chatMessageEditText.setText("");
                Date date = new Date();
                Message message = new Message(chatMessage, senderUid, date.getTime());

                database = FirebaseDatabase.getInstance();
                database.getReference().child("chats")
                        .child(senderRoom)
                        .child("messages")
                        .push().setValue(message).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                database.getReference().child("chats")
                                        .child(receiverRoom)
                                        .child("messages")
                                        .push().setValue(message).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                            }
                                        });
                            }
                        });
            }
        });
    }
}