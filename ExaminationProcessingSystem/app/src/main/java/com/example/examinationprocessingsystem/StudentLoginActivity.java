package com.example.examinationprocessingsystem;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class StudentLoginActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_login);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        emailEditText = findViewById(R.id.editTextRegistrationNumber);
        passwordEditText = findViewById(R.id.editTextPassword);

        TextView signUp = findViewById(R.id.textViewRegister);


        Button loginButton = findViewById(R.id.buttonLogin);

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StudentLoginActivity.this, StudentSignUpActivity.class);
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

                                    db.collection("students")
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
                                                            Intent dashboardIntent = new Intent(StudentLoginActivity.this, StudentDashboardActivity.class);
                                                            dashboardIntent.putExtra("id", documentId);
                                                            startActivity(dashboardIntent);

                                                        } else {
                                                            // Handle the case where no document is found

                                                        }
                                                    } else {
                                                        // Handle failure
                                                    }
                                                }
                                            });

                                    Toast.makeText(StudentLoginActivity.this, "Login successful",
                                            Toast.LENGTH_SHORT).show();

                                } else {
                                    System.out.println(task.getException());
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(StudentLoginActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }

}
