package com.example.examinationprocessingsystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class StaffSignUpActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_sign_up);

        Button signUp = findViewById(R.id.buttonRegister);
        EditText fullname = findViewById(R.id.editTextFullName);
        EditText email = findViewById(R.id.editTextEmail);
        EditText password = findViewById(R.id.editTextPassword);
        CheckBox isAdmin = findViewById(R.id.checkBoxIsAdmin);

        TextView login = findViewById(R.id.textViewLoginLink);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StaffSignUpActivity.this, StaffLoginActivity.class);
                startActivity(intent);
            }
        });
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String fname = String.valueOf(fullname.getText());
                String em = String.valueOf(email.getText());
                String pas = String.valueOf(password.getText());
                boolean admin = isAdmin.isChecked();

                Map<String, Object> staff = new HashMap<>();
                staff.put("name", fname);
                staff.put("email", em);
                staff.put("password", pas);
                staff.put("isAdmin", admin == true ? "true" : "false");


                db.collection("staff")
                        .add(staff)
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
                                                    Toast.makeText(StaffSignUpActivity.this, "Account created.",
                                                            Toast.LENGTH_SHORT).show();
                                                } else {
                                                    // If sign in fails, display a message to the user.
                                                    Toast.makeText(StaffSignUpActivity.this, "Could not create account at this time.",
                                                            Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                System.out.println("DocumentSnapshot added with ID: " + documentReference.getId());
                                Toast.makeText(StaffSignUpActivity.this, "User added successfully!", Toast.LENGTH_SHORT).show();
                                if (Objects.equals(admin, false)) {
                                    Intent intent = new Intent(StaffSignUpActivity.this, MarkEntryActivity.class);
                                    intent.putExtra("id", documentReference.getId());
                                    startActivity(intent);
                                } else {
                                    Intent intent = new Intent(StaffSignUpActivity.this, AdminDashboardActivity.class);
                                    intent.putExtra("id", documentReference.getId());
                                    startActivity(intent);
                                }

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