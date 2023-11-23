package com.example.examinationprocessingsystem;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;

public class CourseRegistrationActivity extends AppCompatActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private int courseCounter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_registration);

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

        Button submit = findViewById(R.id.buttonSubmit);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                
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

        Intent intent = getIntent();
        if (intent != null) {
            String refId = intent.getStringExtra("id");
        }
    }

    private void setCheckedChangeListener(final CheckBox checkBox) {
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Check if the course counter is less than 10 before allowing a checkbox to be checked
                if (isChecked && courseCounter >= 10) {
                    checkBox.setChecked(false); // Prevent checking
                    Toast.makeText(CourseRegistrationActivity.this, "Maximum courses reached (10)", Toast.LENGTH_SHORT).show();
                } else {
                    // Increment or decrement the course counter based on the checked state
                    courseCounter += isChecked ? 1 : -1;
                    // You can update the UI or perform additional actions based on the course counter value
                }
            }
        });
    }
}
