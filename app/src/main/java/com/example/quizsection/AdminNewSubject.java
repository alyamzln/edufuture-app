package com.example.quizsection;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AdminNewSubject extends AppCompatActivity {

    EditText subjectTitle, subjectDescription;
    FloatingActionButton saveSubject;
    ImageView backButton;

    FirebaseFirestore firebaseFirestore;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_new_subject);

        //pass id from layout
        saveSubject=findViewById(R.id.saveSubject);
        subjectTitle=findViewById(R.id.newSubjectName);
        subjectDescription=findViewById(R.id.newSubjectDescription);
        backButton=findViewById(R.id.back_button);

        firebaseFirestore=FirebaseFirestore.getInstance();

        // Direct admin to Admin Course Page
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(AdminNewSubject.this,AdminCoursePage.class);
                startActivity(intent);
                finish();
            }
        });

        // Admin click on save subject button
        saveSubject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String subject = subjectTitle.getText().toString();
                String description = subjectDescription.getText().toString();
                if(subject.isEmpty() || description.isEmpty()){
                    Toast.makeText(getApplicationContext(),"Both field are required",Toast.LENGTH_SHORT).show();
                }
                if(!(subject.contains("Form 1")||subject.contains("Form 2")||subject.contains("Form 3")
                            ||subject.contains("Form 4")||subject.contains("Form 5"))){
                        Toast.makeText(getApplicationContext(),"Need to specify the education level",Toast.LENGTH_SHORT).show();
                }
                else{
                    // Save subject and description in Firebase
                    DocumentReference documentReference=firebaseFirestore.collection("subjects").document(subject);
                    Map<String, Object> subj = new HashMap<>();
                    subj.put("subject", subject);
                    subj.put("description", description);

                    documentReference.set(subj).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(getApplicationContext(),"Subject created successfully",Toast.LENGTH_SHORT).show();
                            // direct back to CoursePage
                            startActivity(new Intent(AdminNewSubject.this,AdminCoursePage.class));
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(),"Failed to create new subject",Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }
        });
    }
}
