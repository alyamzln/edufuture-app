package com.example.quizsection;

import static com.example.quizsection.QuizSubjects.level_id;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.GridView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class QuizChapters extends AppCompatActivity {

    public static List<String> chapList = new ArrayList<>();
    private GridView chap_grid;
    private FirebaseFirestore firestore;
    public static int subj_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_chapters);

        subj_id = getIntent().getIntExtra("SUB_ID", 1);

        chap_grid = findViewById(R.id.chapGridView);

        firestore = FirebaseFirestore.getInstance();

        loadChapters();
    }

    public void loadChapters()
    {
        chapList.clear();

        firestore.collection("QUIZ1").document("LEV" + String.valueOf(level_id))
                .collection("SUB" + String.valueOf(subj_id)).document("CHAPTERS")
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if(task.isSuccessful())
                        {
                            DocumentSnapshot doc = task.getResult();

                            if(doc.exists())
                            {
                                long count = (long)doc.get("NUMCHAP");

                                for (int i=1; i <= count; i++)
                                {
                                    String chapName = doc.getString("CHAP" + String.valueOf(i));
                                    chapList.add(chapName);
                                }

                                ChapAdapter adapter = new ChapAdapter(chapList);
                                chap_grid.setAdapter(adapter);

                            }
                            else
                            {
                                Toast.makeText(QuizChapters.this,"No Chapters Document Exists!",Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }
                        else
                        {
                            Toast.makeText(QuizChapters.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

}