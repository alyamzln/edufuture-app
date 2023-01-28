package com.example.quizsection;

import static com.example.quizsection.QuizChapAdmin.selected_chap_index;
import static com.example.quizsection.QuizChapters.subj_id;
import static com.example.quizsection.QuizLevAdmin.selected_lev_index;
import static com.example.quizsection.QuizQuesAdmin.quesList;
import static com.example.quizsection.QuizQuesAdmin.selected_ques_index;
import static com.example.quizsection.QuizSubAdmin.selected_sub_index;
import static com.example.quizsection.QuizSubjects.level_id;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class QuizActivityAdmin extends AppCompatActivity {

    private EditText ques, optionA, optionB, optionC, optionD, answer;
    private ImageView back;
    private Button updateQB;
    private List<QuestionModel> quesList = new ArrayList<>();
    private String qStr, aStr, bStr, cStr, dStr, ansStr;
    private Dialog loadingDialog;
    private FirebaseFirestore firestore;
    private String action;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_admin);

        back = findViewById(R.id.back_quiz_admin_question);
        ques = findViewById(R.id.quizQuestion);
        optionA = findViewById(R.id.optionA);
        optionB = findViewById(R.id.optionB);
        optionC = findViewById(R.id.optionC);
        optionD = findViewById(R.id.optionD);
        answer = findViewById(R.id.correctAns);
        updateQB = findViewById(R.id.updateButton);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(QuizActivityAdmin.this, QuizQuesAdmin.class);
                startActivity(i);
                finish();
            }
        });

        loadingDialog = new Dialog(QuizActivityAdmin.this);
        loadingDialog.setContentView(R.layout.loading_progressbar);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawableResource(R.drawable.progress_background);
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        firestore = FirebaseFirestore.getInstance();

        updateQB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                updateQuizQues();

            }
        });

        loadQuizData();


    }

    private void loadQuizData() {

        loadingDialog.show();

        firestore.collection("QUIZ1").document("LEV" + selected_lev_index)
                .collection("SUB" + selected_sub_index).document("CHAPTERS")
                .collection("CHAP" + selected_chap_index).document("QUESTION" + selected_ques_index)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if (task.isSuccessful())
                        {
                            DocumentSnapshot doc = task.getResult();

                            if(doc.exists())
                            {
                                quesList.add(new QuestionModel(

                                        doc.getString("QUESTION"),
                                        doc.getString("A"),
                                        doc.getString("B"),
                                        doc.getString("C"),
                                        doc.getString("D"),
                                        Integer.valueOf(doc.getString("ANSWER"))

                                ));

                                ques.setText(doc.getString("QUESTION"));
                                optionA.setText(doc.getString("A"));
                                optionB.setText(doc.getString("B"));
                                optionC.setText(doc.getString("C"));
                                optionD.setText(doc.getString("D"));
                                answer.setText(String.valueOf(doc.getString("ANSWER")));

                            }

                            else
                            {
                                Toast.makeText(QuizActivityAdmin.this,"No Quiz Question Document Exists!",Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }

                        else{

                            Toast.makeText(QuizActivityAdmin.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                        }

                        loadingDialog.dismiss();
                    }
                });

    }

    private void updateQuizQues()
    {
        loadingDialog.show();

        qStr = ques.getText().toString();
        aStr = optionA.getText().toString();
        bStr = optionB.getText().toString();
        cStr = optionC.getText().toString();
        dStr = optionD.getText().toString();
        ansStr = answer.getText().toString();

        Map<String,Object> quesData = new ArrayMap<>();
        quesData.put("QUESTION", qStr);
        quesData.put("A", aStr);
        quesData.put("B", bStr);
        quesData.put("C", cStr);
        quesData.put("D", dStr);
        quesData.put("ANSWER", ansStr);

        firestore.collection("QUIZ1").document("LEV" + selected_lev_index)
                .collection("SUB" + selected_sub_index).document("CHAPTERS")
                .collection("CHAP" + selected_chap_index).document("QUESTION" + selected_ques_index)
                .set(quesData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                        Toast.makeText(QuizActivityAdmin.this,"Quiz question updated successfully!",Toast.LENGTH_SHORT).show();

                        loadingDialog.dismiss();


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(QuizActivityAdmin.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                        loadingDialog.dismiss();

                    }
                });

    }

}