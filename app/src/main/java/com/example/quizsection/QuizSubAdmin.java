package com.example.quizsection;

import static com.example.quizsection.QuizLevAdmin.selected_lev_index;

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
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class QuizSubAdmin extends AppCompatActivity {

    private RecyclerView sub_recycler_view;
    private ImageView back;
    private Button addSub;
    public static List<String> subjList = new ArrayList<>();
    public static int selected_sub_index = 0;
    private FirebaseFirestore firestore;
    private Dialog loadingDialog, addSubDialog;
    private EditText dialogSubName;
    private Button dialogAddSub;
    private AdminSubAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_sub_admin);

        sub_recycler_view = findViewById(R.id.subRecyler);
        back = findViewById(R.id.back_quiz_admin1);
        addSub = findViewById(R.id.addSub);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(QuizSubAdmin.this, QuizLevAdmin.class);
                startActivity(i);
                finish();
            }
        });

        loadingDialog = new Dialog(QuizSubAdmin.this);
        loadingDialog.setContentView(R.layout.loading_progressbar);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawableResource(R.drawable.progress_background);
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);

        addSubDialog = new Dialog(QuizSubAdmin.this);
        addSubDialog.setContentView(R.layout.add_item_dialog);
        addSubDialog.setCancelable(true);
        addSubDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);

        dialogSubName = addSubDialog.findViewById(R.id.edit_item);
        dialogAddSub = addSubDialog.findViewById(R.id.edit_btn);

        firestore = FirebaseFirestore.getInstance();

        addSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialogSubName.getText().clear();
                addSubDialog.show();

            }
        });

        dialogAddSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(dialogSubName.getText().toString().isEmpty())
                {
                    dialogSubName.setError("Enter subject name");
                    return;
                }

                addNewSub(dialogSubName.getText().toString());
            }
        });

        firestore = FirebaseFirestore.getInstance();

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        sub_recycler_view.setLayoutManager(layoutManager);

        loadSubjects();
    }

    public void loadSubjects()
    {
        loadingDialog.show();

        subjList.clear();

        firestore.collection("QUIZ1").document("LEV" + selected_lev_index)
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

                                adapter = new AdminSubAdapter(subjList);
                                sub_recycler_view.setAdapter(adapter);

                            }
                            else
                            {
                                Toast.makeText(QuizSubAdmin.this,"No Subject Document Exists!",Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }
                        else
                        {
                            Toast.makeText(QuizSubAdmin.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                        }

                        loadingDialog.dismiss();

                    }
                });
    }

    private void addNewSub(String subject)
    {
        addSubDialog.dismiss();
        loadingDialog.show();

        DocumentReference documentReference = firestore.collection("QUIZ1").document("LEV" + selected_lev_index);

        Map<String,Object> subData = new ArrayMap<>();
        subData.put("NUMCHAP", 0);

        firestore.collection("QUIZ1").document("LEV" + selected_lev_index)
                .collection("SUB" + (subjList.size() + 1)).document("CHAPTERS")
                .set(subData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                        Map<String,Object> subDoc = new ArrayMap<>();
                        subDoc.put("SUB" + (subjList.size() + 1) , subject);
                        subDoc.put("NUMSUB" , (subjList.size() + 1));

                        firestore.collection("QUIZ1").document("LEV" + selected_lev_index)
                                .update(subDoc)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {

                                        Toast.makeText(QuizSubAdmin.this, "Subject added successfully",Toast.LENGTH_SHORT).show();


                                        subjList.add(subject);
                                        adapter.notifyItemInserted(subjList.size());

                                        loadingDialog.dismiss();

                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                        Toast.makeText(QuizSubAdmin.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                                        loadingDialog.dismiss();

                                    }
                                });

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(QuizSubAdmin.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                        loadingDialog.dismiss();

                    }
                });
    }
}