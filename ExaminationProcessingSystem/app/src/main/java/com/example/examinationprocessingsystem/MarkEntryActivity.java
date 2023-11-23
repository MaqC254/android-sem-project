package com.example.examinationprocessingsystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.HashMap;
import java.util.Map;

public class MarkEntryActivity extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mark_entry);

        Intent intent = getIntent();
        String courseId = intent.getStringExtra("course");
        System.out.println("course ->"  + courseId);

        EditText reg_no = findViewById(R.id.editTextCourse);
        EditText ass1 = findViewById(R.id.editTextAssignment1);
        EditText ass2 = findViewById(R.id.editTextAssignment2);
        EditText cat1 = findViewById(R.id.editTextCat1);
        EditText cat2 = findViewById(R.id.editTextCat2);
        EditText exam = findViewById(R.id.editTextExam);
        Button button = findViewById(R.id.buttonSubmit);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String reg = String.valueOf(reg_no.getText());
                db.collection("students")
                        .whereEqualTo("reg_no", reg)
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
                                        String catOneGrade = String.valueOf(cat1.getText());
                                        String catTwoGrade = String.valueOf(cat2.getText());
                                        String assOneGrade = String.valueOf(ass1.getText());
                                        String assTwoGrade = String.valueOf(ass2.getText());
                                        String examGrade = String.valueOf(exam.getText());
                                        int total = (int) ((int) (Integer.parseInt(catOneGrade) + Integer.parseInt(catTwoGrade) + Integer.parseInt(assOneGrade) + Integer.parseInt(assTwoGrade)) * 0.4 + (Integer.parseInt(examGrade)));

                                        Map<String, Object> marks = new HashMap<>();
                                        marks.put("cat1", catOneGrade);
                                        marks.put("cat2", catTwoGrade);
                                        marks.put("ass1", assOneGrade);
                                        marks.put("ass2", assTwoGrade);
                                        marks.put("exam", examGrade);
                                        marks.put("total", String.valueOf(total));
                                        marks.put("pass", total >= 70 ? "pass" : "fail" );
                                        db.collection("marks")
                                                .whereEqualTo("unit_code", courseId)
                                                .whereEqualTo("student", documentId)
                                                .get()
                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                        if (task.isSuccessful()) {
                                                            if (!task.getResult().getDocuments().isEmpty()) {
                                                                // Access the first document in the result
                                                                DocumentSnapshot document = task.getResult().getDocuments().get(0);
                                                                String marksdocumentId = document.getId();
                                                                db.collection("marks")
                                                                        .document(marksdocumentId)
                                                                        .update(marks)
                                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                            @Override
                                                                            public void onSuccess(Void aVoid) {
                                                                                Toast.makeText(MarkEntryActivity.this, "Updated marks successfully", Toast.LENGTH_SHORT).show();
                                                                            }
                                                                        }).addOnFailureListener(new OnFailureListener() {
                                                                            @Override
                                                                            public void onFailure(@NonNull Exception e) {
                                                                                Toast.makeText(MarkEntryActivity.this, "Cannot update marks", Toast.LENGTH_SHORT).show();
                                                                            }
                                                                        });
                                                            }
                                                        }
                                                    }
                                                });
                                    }
                                }
                            }
                        });
            }
        });
    }
}