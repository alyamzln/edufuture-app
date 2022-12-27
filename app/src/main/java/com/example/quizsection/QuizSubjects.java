package com.example.quizsection;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.widget.GridView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class QuizSubjects extends AppCompatActivity {

    public static List<String> subjList = new ArrayList<>();
    private GridView subj_grid;
    private FirebaseFirestore firestore;
    public static int level_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_subjects);

        level_id = getIntent().getIntExtra("LEVEL_ID", 1);

        subj_grid = findViewById(R.id.subjGridView);

        firestore = FirebaseFirestore.getInstance();

        loadSubjects();

    }

    public void loadSubjects()
    {
        subjList.clear();

        firestore.collection("QUIZ1").document("LEV" + String.valueOf(level_id))
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if(task.isSuccessful())
                        {
                            DocumentSnapshot doc = task.getResult();

                            if(doc.exists())
                            {
                                long count = (long)doc.get("NUMSUB");

                                for (int i=1; i <= count; i++)
                                {
                                    String subjName = doc.getString("SUB" + String.valueOf(i));
                                    subjList.add(subjName);
                                }

                                SubjGridAdapter adapter = new SubjGridAdapter(subjList);
                                subj_grid.setAdapter(adapter);

                            }
                            else
                            {
                                Toast.makeText(QuizSubjects.this,"No Level Document Exists!",Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }
                        else
                        {
                            Toast.makeText(QuizSubjects.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

}