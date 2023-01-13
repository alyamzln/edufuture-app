package com.example.quizsection;

import static com.example.quizsection.QuizLevAdmin.selected_lev_index;
import static com.example.quizsection.QuizQuesAdmin.quesList;
import static com.example.quizsection.QuizSubAdmin.selected_sub_index;
import static com.example.quizsection.QuizSubjects.level_id;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.os.Bundle;
import android.util.ArrayMap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class QuizChapAdmin extends AppCompatActivity {

    private RecyclerView chap_recycler_view;
    private Button addChap;
    public static List<String> chapList = new ArrayList<>();
    public static int selected_chap_index = 0;
    private FirebaseFirestore firestore;
    private Dialog loadingDialog, addChapDialog;
    private EditText dialogChapName;
    private Button dialogAddChap;
    private AdminChapAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_chap_admin);

        chap_recycler_view = findViewById(R.id.chapRecyler);
        addChap = findViewById(R.id.addChap);

        loadingDialog = new Dialog(QuizChapAdmin.this);
        loadingDialog.setContentView(R.layout.loading_progressbar);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawableResource(R.drawable.progress_background);
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);

        addChapDialog = new Dialog(QuizChapAdmin.this);
        addChapDialog.setContentView(R.layout.add_item_dialog);
        addChapDialog.setCancelable(true);
        addChapDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);

        dialogChapName = addChapDialog.findViewById(R.id.edit_item);
        dialogAddChap = addChapDialog.findViewById(R.id.edit_btn);

        firestore = FirebaseFirestore.getInstance();

        addChap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialogChapName.getText().clear();
                addChapDialog.show();

            }
        });

        dialogAddChap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(dialogChapName.getText().toString().isEmpty())
                {
                    dialogChapName.setError("Enter chapter name");
                    return;
                }

                addNewChap(dialogChapName.getText().toString());
            }
        });

        firestore = FirebaseFirestore.getInstance();

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        chap_recycler_view.setLayoutManager(layoutManager);

        loadChapters();

    }

    public void loadChapters()
    {
        loadingDialog.show();

        chapList.clear();

        firestore.collection("QUIZ1").document("LEV" + selected_lev_index)
                .collection("SUB" + selected_sub_index).document("CHAPTERS")
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

                                adapter = new AdminChapAdapter(chapList);
                                chap_recycler_view.setAdapter(adapter);

                            }
                            else
                            {
                                Toast.makeText(QuizChapAdmin.this,"No Subject Document Exists!",Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }
                        else
                        {
                            Toast.makeText(QuizChapAdmin.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                        }

                        loadingDialog.dismiss();

                    }
                });
    }

    private void addNewChap(String subject)
    {
        addChapDialog.dismiss();
        loadingDialog.show();

        DocumentReference documentReference = firestore.collection("QUIZ1").document("LEV" + selected_lev_index)
                .collection("SUB" + selected_sub_index).document("CHAPTERS");

        Map<String,Object> chapDoc = new ArrayMap<>();
        chapDoc.put("CHAP" + (chapList.size() + 1) , subject);
        chapDoc.put("NUMCHAP" , (chapList.size() + 1));

        firestore.collection("QUIZ1").document("LEV" + selected_lev_index)
                .collection("SUB" + selected_sub_index).document("CHAPTERS")
                .update(chapDoc)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                        Map<String,Object> quesDoc = new ArrayMap<>();
                        quesDoc.put("NUMQUES" , 0);

                        firestore.collection("QUIZ1").document("LEV" + selected_lev_index)
                                .collection("SUB" + selected_sub_index).document("CHAPTERS").collection("CHAP" + (chapList.size() + 1))
                                .document("Questions")
                                .set(quesDoc)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {

                                        Toast.makeText(QuizChapAdmin.this, "Chapter added successfully",Toast.LENGTH_SHORT).show();


                                        chapList.add(subject);
                                        adapter.notifyItemInserted(chapList.size());

                                        loadingDialog.dismiss();

                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                        Toast.makeText(QuizChapAdmin.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                                        loadingDialog.dismiss();

                                    }
                                });

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                         Toast.makeText(QuizChapAdmin.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                         loadingDialog.dismiss();
                     }
                });
     }

}