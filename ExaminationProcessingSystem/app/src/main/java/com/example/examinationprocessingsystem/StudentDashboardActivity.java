package com.example.examinationprocessingsystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

// Import statements
// Import statements

public class StudentDashboardActivity extends AppCompatActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_dashboard);

        // Get the student ID from the intent
        Intent intent = getIntent();
        String studentId = intent.getStringExtra("id");

        // Find views
        TextView textViewStudentName = findViewById(R.id.textViewStudentName);
        ListView listViewCourses = findViewById(R.id.listViewCourses);
        TextView textViewMeanGrade = findViewById(R.id.textViewMeanGrade);
        TextView textViewPassFail = findViewById(R.id.textViewPassFail);

        // Fetch and display student information
        fetchAndDisplayStudentInfo(studentId, textViewStudentName, listViewCourses, textViewMeanGrade, textViewPassFail);
    }

    private void fetchAndDisplayStudentInfo(String studentId, TextView textViewStudentName, ListView listViewCourses, TextView textViewMeanGrade, TextView textViewPassFail) {
        // Fetch student information (replace "students" with your actual collection name)
        DocumentReference studentRef = db.collection("students").document(studentId);
        studentRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            // Get student name
                            String studentName = documentSnapshot.getString("name");
                            textViewStudentName.setText(studentName);

                            // Fetch and display courses and marks
                            fetchAndDisplayCoursesAndMarks(studentId, listViewCourses, textViewMeanGrade, textViewPassFail);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle failure
                        Toast.makeText(StudentDashboardActivity.this, "Error fetching student information", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void fetchAndDisplayCoursesAndMarks(String studentId, ListView listViewCourses, TextView textViewMeanGrade, TextView textViewPassFail) {
        // Fetch courses and marks for the student (replace "marks" with your actual collection name)
        db.collection("marks")
                .whereEqualTo("student", studentId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<String> coursesList = new ArrayList<>();
                            int totalMarks = 0;
                            int numberOfCourses = 0;
                            int total;

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // Get unit_code and pass from the document
                                String unitCode = document.getString("unit_code");
                                String pass = document.getString("pass");
                                String average = document.getString("average");

                                // Calculate total marks based on your logic
                                // For simplicity, assuming you have a field called "total" in your document
                                try {
                                    total = document.getLong("total").intValue();
                                } catch (Exception e) {
                                    total = 0;
                                }

                                // Display unit_code and pass in your list item (customize as needed)
                                String listItem = String.format("%-20s - %-10s %s", unitCode, average, pass);
                                coursesList.add(listItem);

                                // Calculate total marks and number of courses
                                totalMarks += total;
                                numberOfCourses++;
                            }

                            // Display courses in the ListView
                            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(StudentDashboardActivity.this, R.layout.list_item_course, coursesList);
                            listViewCourses.setAdapter(arrayAdapter);

                            // Calculate and display mean grade and pass/fail status
                            calculateAndDisplayMeanGrade(totalMarks, numberOfCourses, textViewMeanGrade, textViewPassFail);
                        } else {
                            // Handle failure
                            Toast.makeText(StudentDashboardActivity.this, "Error fetching courses and marks", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void calculateAndDisplayMeanGrade(int totalMarks, int numberOfCourses, TextView textViewMeanGrade, TextView textViewPassFail) {
        // Calculate mean grade based on your logic
        // For simplicity, assuming you have a field called "average" in your document
        double meanGrade = totalMarks / (double) numberOfCourses;

        // Display mean grade
        textViewMeanGrade.setText("Mean Grade: " + meanGrade);

        // Determine pass/fail based on your logic
        // For simplicity, assuming you have a field called "pass" in your document
        String passFailStatus = meanGrade == 0? "" : (meanGrade >= 70) ? "Pass" : "Fail";

        // Display pass/fail status
        textViewPassFail.setText("Pass/Fail: " + passFailStatus);
    }
}
