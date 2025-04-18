package com.example.glassy;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Settings extends AppCompatActivity {
    private String email;
    private String password;
    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private EditText usernameSettingsEditText;
    private EditText statusSettingsEditText;
    private Button saveSettingsButton;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        usernameSettingsEditText = findViewById(R.id.usernameSettingsEditText);
        statusSettingsEditText = findViewById(R.id.statusSettingsEditText);

        DatabaseReference reference = database.getReference().child("user").child(auth.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                email = snapshot.child("email").getValue().toString();
                password = snapshot.child("password").getValue().toString();
                String username = snapshot.child("username").getValue().toString();
                String status = snapshot.child("status").getValue().toString();
                usernameSettingsEditText.setText(username);
                statusSettingsEditText.setText(status);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        saveSettingsButton = findViewById(R.id.saveSettingsButton);
        saveSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameSettingsEditText.getText().toString();
                String status = statusSettingsEditText.getText().toString();

                progressBar.setVisibility(View.VISIBLE);

                User users = new User(auth.getUid(), username, email, password, status);

                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("user").child(auth.getUid());
                reference.setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(Settings.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                        else {
                            Toast.makeText(Settings.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
}