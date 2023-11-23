package com.example.examinationprocessingsystem;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class CourseRegistrationActivity extends AppCompatActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private int courseCounter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_registration);
        Intent intent = getIntent();
        String documentId = intent.getStringExtra("id");

        CheckBox unit1, unit2, unit3, unit4, unit5, unit7, unit8, unit9, unit10, unit11, unit12;

        unit1 = findViewById(R.id.unit1);
        unit2 = findViewById(R.id.unit2);
        unit3 = findViewById(R.id.unit3);
        unit4 = findViewById(R.id.unit4);
        unit5 = findViewById(R.id.unit5);
        unit7 = findViewById(R.id.unit7);
        unit8 = findViewById(R.id.unit8);
        unit9 = findViewById(R.id.unit9);
        unit10 = findViewById(R.id.unit10);
        unit11 = findViewById(R.id.unit11);
        unit12 = findViewById(R.id.unit12);

        Button submit = findViewById(R.id.button);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Obtain the values of the selected checkboxes and create a dictionary of the units
                Map<String, Object> selectedUnits = getSelectedUnits(unit1, unit2, unit3, unit4, unit5, unit7, unit8, unit9, unit10, unit11, unit12);

                // Update the Firestore document with the selected units
                db.collection("students")
                        .document(documentId)
                        .update(selectedUnits)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                for (Map.Entry<String, Object> entry : selectedUnits.entrySet()) {
                                    String key = entry.getKey();
                                    Object value = entry.getValue();

                                    if (value.toString() == "true") {
                                        Map<String, Object> marks = new HashMap<>();
                                        marks.put("student", documentId);
                                        marks.put("unit_code", key);
                                        marks.put("cat1", "-");
                                        marks.put("cat2", "-");
                                        marks.put("ass1", "-");
                                        marks.put("ass2", "-");
                                        marks.put("exam", "-");
                                        marks.put("total", "-");
                                        marks.put("average", "-");
                                        marks.put("pass", "");

                                        db.collection("marks")
                                                .add(marks)
                                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                    @Override
                                                    public void onSuccess(DocumentReference documentReference) {
                                                        System.out.println("Marks added successfully");
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        System.out.println("Error adding document" + e);
                                                    }
                                                });
                                    }
                                }
                                Intent dashboardIntent = new Intent(CourseRegistrationActivity.this, StudentDashboardActivity.class);
                                dashboardIntent.putExtra("id", documentId);
                                // Successfully updated the document
                                Toast.makeText(CourseRegistrationActivity.this, "Course registration successful", Toast.LENGTH_SHORT).show();
                                startActivity(dashboardIntent);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Handle the failure to update the document
                                Toast.makeText(CourseRegistrationActivity.this, "Error updating course registration", Toast.LENGTH_SHORT).show();
                            }
                        });

                // Set OnCheckedChangeListener for each CheckBox
                setCheckedChangeListener(unit1);
                setCheckedChangeListener(unit2);
                setCheckedChangeListener(unit3);
                setCheckedChangeListener(unit4);
                setCheckedChangeListener(unit5);
                setCheckedChangeListener(unit7);
                setCheckedChangeListener(unit8);
                setCheckedChangeListener(unit9);
                setCheckedChangeListener(unit10);
                setCheckedChangeListener(unit11);
                setCheckedChangeListener(unit12);

                if (intent != null) {
                    String refId = intent.getStringExtra("id");
                }
            }

            private void setCheckedChangeListener(final CheckBox checkBox) {
                checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        // Increment or decrement the course counter based on the checked state
                        courseCounter += isChecked ? 1 : -1;

                        // Check if the course counter is less than 10 before allowing a checkbox to be checked
                        if (isChecked && courseCounter > 10) {
                            checkBox.setChecked(false); // Prevent checking
                            Toast.makeText(CourseRegistrationActivity.this, "Maximum courses reached (10)", Toast.LENGTH_SHORT).show();
                        } else {
                            // You can update the UI or perform additional actions based on the course counter value
                        }
                    }
                });
            }


            private Map<String, Object> getSelectedUnits(CheckBox... checkboxes) {
                Map<String, Object> selectedUnits = new HashMap<>();
                for (CheckBox checkbox : checkboxes) {
                    String unitName = checkbox.getText().toString();
                    boolean isChecked = checkbox.isChecked();
                    selectedUnits.put(unitName, isChecked);
                }
                return selectedUnits;
            }
        });
    }
}