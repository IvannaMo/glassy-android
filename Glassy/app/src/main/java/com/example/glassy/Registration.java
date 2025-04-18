package com.example.glassy;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Registration extends AppCompatActivity {
    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private EditText usernameRegEditText;
    private EditText emailRegEditText;
    private EditText passwordRegEditText;
    private EditText confirmPasswordRegEditText;
    private Button registrationButton;
    private TextView loginTextView;
    private String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registration);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        usernameRegEditText = findViewById(R.id.usernameRegEditText);
        emailRegEditText = findViewById(R.id.emailRegEditText);
        passwordRegEditText = findViewById(R.id.passwordRegEditText);
        confirmPasswordRegEditText = findViewById(R.id.confirmPasswordRegEditText);

        loginTextView = findViewById(R.id.loginTextView);
        loginTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Registration.this, Login.class);
                startActivity(intent);
                finish();
            }
        });

        registrationButton = findViewById(R.id.registrationButton);
        registrationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameRegEditText.getText().toString();
                String email = emailRegEditText.getText().toString();
                String password = passwordRegEditText.getText().toString();
                String confirmPassword = confirmPasswordRegEditText.getText().toString();
                String status = "Hello world!";
                progressBar.setVisibility(View.VISIBLE);

                if (TextUtils.isEmpty(username) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)){
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(Registration.this, "Please fill in all the required fields", Toast.LENGTH_SHORT).show();
                }
                else if (!email.matches(emailPattern)) {
                    progressBar.setVisibility(View.GONE);
                    emailRegEditText.setError("Please provide a valid email address");
                }
                else if (password.length() < 6) {
                    progressBar.setVisibility(View.GONE);
                    passwordRegEditText.setError("Password must be at least 6 characters");
                }
                else if (!password.equals(confirmPassword)) {
                    progressBar.setVisibility(View.GONE);
                    passwordRegEditText.setError("Passwords do not match");
                }
                else {
                    auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                String userId = task.getResult().getUser().getUid();
                                DatabaseReference reference = database.getReference().child("user").child(userId);

                                User user = new User(userId, username, email, password, status);
                                reference.setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            progressBar.setVisibility(View.VISIBLE);

                                            Intent intent = new Intent(Registration.this, MainActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                        else {
                                            progressBar.setVisibility(View.GONE);
                                            Toast.makeText(Registration.this, "Error in creating the user", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                            else {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(Registration.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
}