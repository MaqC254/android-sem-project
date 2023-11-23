package com.example.examinationprocessingsystem;

import static androidx.fragment.app.FragmentManager.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class StudentSignUpActivity extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_sign_up);

        EditText fullname = findViewById(R.id.editTextFullName);
        EditText reg_no = findViewById(R.id.editTextRegistrationNumber);
        EditText email = findViewById(R.id.editTextEmail);
        EditText password = findViewById(R.id.editTextPassword);
        Button signUp = findViewById(R.id.buttonSignUp);
        TextView login = findViewById(R.id.textViewLoginLink);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StudentSignUpActivity.this, StudentLoginActivity.class);
                startActivity(intent);
            }
        });
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String fname = String.valueOf(fullname.getText());
                String reg = String.valueOf(reg_no.getText());
                String em = String.valueOf(email.getText());
                String pas = String.valueOf(password.getText());

                Map<String, Object> student = new HashMap<>();
                student.put("name", fname);
                student.put("reg_no", reg);
                student.put("email", em);
                student.put("password", pas);
                student.put("courses", "");


                db.collection("students")
                        .add(student)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                mAuth.createUserWithEmailAndPassword(em, pas)
                                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                if (task.isSuccessful()) {
                                                    // Sign in success, update UI with the signed-in user's information
                                                    // FirebaseUser user = mAuth.getCurrentUser();
                                                    Toast.makeText(StudentSignUpActivity.this, "Account created.",
                                                            Toast.LENGTH_SHORT).show();
                                                } else {
                                                    // If sign in fails, display a message to the user.
                                                    Toast.makeText(StudentSignUpActivity.this, "Could not create account at this time.",
                                                            Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                System.out.println("DocumentSnapshot added with ID: " + documentReference.getId());
                                Toast.makeText(StudentSignUpActivity.this, "User added successfully!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(StudentSignUpActivity.this, CourseRegistrationActivity.class);
                                intent.putExtra("id", documentReference.getId());
                                startActivity(intent);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                System.out.println("Error adding document" + e);
                            }
                        });
            }
        });


    }
}