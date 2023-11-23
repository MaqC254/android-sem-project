package com.example.examinationprocessingsystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;

public class StaffLoginActivity extends AppCompatActivity {
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_login);

        EditText emailEditText = findViewById(R.id.editTextEmail);
        EditText passwordEditText = findViewById(R.id.editTextPassword);
        Button loginButton = findViewById(R.id.buttonLogin);
        TextView signUp = findViewById(R.id.textViewSignUpLink);
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StaffLoginActivity.this, StaffSignUpActivity.class);
                startActivity(intent);
            }
        });
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();

                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, go to profiles activity
                                    FirebaseUser user = mAuth.getCurrentUser();

                                    db.collection("staff")
                                            .whereEqualTo("email", email)
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        // Check if there is at least one document
                                                        if (!task.getResult().getDocuments().isEmpty()) {
                                                            // Access the first document in the result
                                                            DocumentSnapshot document = task.getResult().getDocuments().get(0);

                                                            // Access the document ID
                                                            String documentId = document.getId();
                                                            String isAdmin = document.getString("isAdmin");
                                                            if (Objects.equals(isAdmin.toString(), "true")) {
                                                                Intent dashboardIntent = new Intent(StaffLoginActivity.this, AdminDashboardActivity.class);
                                                                dashboardIntent.putExtra("id", documentId);
                                                                startActivity(dashboardIntent);
                                                            } else if(isAdmin != null && Objects.equals(isAdmin.toString(), "false")){
                                                                Intent dashboardIntent = new Intent(StaffLoginActivity.this, MarkEntryActivity.class);
                                                                startActivity(dashboardIntent);
                                                                startActivity(dashboardIntent);
                                                            }

                                                        } else {
                                                            // Handle the case where no document is found

                                                        }
                                                    } else {
                                                        // Handle failure
                                                    }
                                                }
                                            });

                                    Toast.makeText(StaffLoginActivity.this, "Login successful",
                                            Toast.LENGTH_SHORT).show();

                                } else {
                                    System.out.println(task.getException());
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(StaffLoginActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }
}