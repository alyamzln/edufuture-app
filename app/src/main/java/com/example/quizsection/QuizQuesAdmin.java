package com.example.quizsection;

import static com.example.quizsection.QuizChapAdmin.selected_chap_index;
import static com.example.quizsection.QuizChapters.subj_id;
import static com.example.quizsection.QuizLevAdmin.selected_lev_index;
import static com.example.quizsection.QuizSubAdmin.selected_sub_index;
import static com.example.quizsection.QuizSubjects.level_id;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.ArrayMap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class QuizQuesAdmin extends AppCompatActivity {

    private RecyclerView ques_recycler_view;
    private ImageView back;
    private Button addQues;
    public static List<String> quesList = new ArrayList<>();
    public static int selected_ques_index = 0;
    private FirebaseFirestore firestore;
    private Dialog loadingDialog, addQuesDialog;
    private EditText dialogQuesName;
    private Button dialogAddQues;
    private AdminQuesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_ques_admin);

        ques_recycler_view = findViewById(R.id.quesRecyler);
        back = findViewById(R.id.back_quiz_admin3);
        addQues = findViewById(R.id.addQues);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(QuizQuesAdmin.this, QuizChapAdmin.class);
                startActivity(i);
                finish();
            }
        });

        loadingDialog = new Dialog(QuizQuesAdmin.this);
        loadingDialog.setContentView(R.layout.loading_progressbar);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawableResource(R.drawable.progress_background);
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);

        firestore = FirebaseFirestore.getInstance();

        addQues.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                addNewQues();

            }
        });

        firestore = FirebaseFirestore.getInstance();

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        ques_recycler_view.setLayoutManager(layoutManager);

        loadQuestions();

    }

    public void loadQuestions()
    {
        quesList.clear();

        loadingDialog.show();

        firestore.collection("QUIZ1").document("LEV" + selected_lev_index)
                .collection("SUB" + selected_sub_index).document("Questions")
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if(task.isSuccessful())
                        {
                            DocumentSnapshot doc = task.getResult();

                            if(doc.exists())
                            {
                                long count = (long)doc.get("NUMQUES");

                                for (int i=1; i <= count; i++)
                                {
                                    String quesName = doc.getString("QUES" + String.valueOf(i));

                                    quesList.add(quesName);
                                }

                                adapter = new AdminQuesAdapter(quesList);
                                ques_recycler_view.setAdapter(adapter);

                            }
                            else
                            {
                                Toast.makeText(QuizQuesAdmin.this,"No Question Document Exists!",Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }
                        else
                        {
                            Toast.makeText(QuizQuesAdmin.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                        }

                        loadingDialog.dismiss();

                    }
                });
    }

    private void addNewQues()
    {
        loadingDialog.show();

        CollectionReference collectionReference = firestore.collection("QUIZ1").document("LEV" + selected_lev_index)
                .collection("SUB" + selected_sub_index).document("CHAPTERS").collection("CHAP" + selected_chap_index);

        Map<String,Object> quesDoc = new ArrayMap<>();
        quesDoc.put("QUES" + (quesList.size() + 1) , "QUESTION " + (quesList.size() + 1));
        quesDoc.put("NUMQUES" , (quesList.size() + 1));

        firestore.collection("QUIZ1").document("LEV" + selected_lev_index)
                .collection("SUB" + selected_sub_index).document("Questions")
                .update(quesDoc)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                        Map<String,Object> quesDoc = new ArrayMap<>();
                        quesDoc.put("QUESTION", "Question");
                        quesDoc.put("ANSWER" , "0");
                        quesDoc.put("A" , "Option A");
                        quesDoc.put("B" , "Option B");
                        quesDoc.put("C" , "Option C");
                        quesDoc.put("D" , "Option D");

                        firestore.collection("QUIZ1").document("LEV" + selected_lev_index)
                                .collection("SUB" + selected_sub_index).document("CHAPTERS").collection("CHAP" + selected_chap_index)
                                .document("QUESTION" + (quesList.size() + 1))
                                .set(quesDoc)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {

                                        Toast.makeText(QuizQuesAdmin.this, "Question added successfully",Toast.LENGTH_SHORT).show();


                                        quesList.add("QUESTION " + (quesList.size() + 1));
                                        adapter.notifyItemInserted(quesList.size());

                                        loadingDialog.dismiss();

                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                        Toast.makeText(QuizQuesAdmin.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                                        loadingDialog.dismiss();

                                    }
                                });


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(QuizQuesAdmin.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                        loadingDialog.dismiss();
                    }
                });
    }
}