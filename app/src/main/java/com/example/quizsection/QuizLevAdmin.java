package com.example.quizsection;

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

public class QuizLevAdmin extends AppCompatActivity {

    private RecyclerView lev_recycler_view;
    private Button addLev;
    public static List<LevelModel> levList = new ArrayList<>();
    public static int selected_lev_index = 0;
    private FirebaseFirestore firestore;
    private Dialog loadingDialog, addLevDialog;
    private EditText dialogLevName;
    private Button dialogAddLev;
    private AdminLevAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_lev_admin);

        lev_recycler_view = findViewById(R.id.levelRecyler);
        addLev = findViewById(R.id.addLev);

        loadingDialog = new Dialog(QuizLevAdmin.this);
        loadingDialog.setContentView(R.layout.loading_progressbar);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawableResource(R.drawable.progress_background);
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);

        addLevDialog = new Dialog(QuizLevAdmin.this);
        addLevDialog.setContentView(R.layout.add_item_dialog);
        addLevDialog.setCancelable(true);
        addLevDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);

        dialogLevName = addLevDialog.findViewById(R.id.edit_item);
        dialogAddLev = addLevDialog.findViewById(R.id.edit_btn);

        firestore = FirebaseFirestore.getInstance();

        addLev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialogLevName.getText().clear();
                addLevDialog.show();
            }
        });

        dialogAddLev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(dialogLevName.getText().toString().isEmpty())
                {
                    dialogLevName.setError("Enter level name");
                    return;
                }

                addNewLev(dialogLevName.getText().toString());
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        lev_recycler_view.setLayoutManager(layoutManager);

        loadData();

    }

    private void loadData()
    {
        loadingDialog.show();

        levList.clear();

        firestore.collection("QUIZ1").document("Level")
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if(task.isSuccessful())
                        {
                            DocumentSnapshot doc = task.getResult();

                            if(doc.exists())
                            {
                                long count = (long)doc.get("Count");

                                for (int i=1; i <= count; i++)
                                {
                                    String levelName = doc.getString("LEV" + String.valueOf(i));

                                    levList.add(new LevelModel(levelName, "0"));
                                }

                                adapter = new AdminLevAdapter(levList);
                                lev_recycler_view.setAdapter(adapter);

                            }
                            else
                            {
                                Toast.makeText(QuizLevAdmin.this,"No Level Document Exists!",Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }
                        else
                        {
                            Toast.makeText(QuizLevAdmin.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                        }

                        loadingDialog.dismiss();
                    }
                });
    }

    private void addNewLev(String title)
    {
        addLevDialog.dismiss();
        loadingDialog.show();

        DocumentReference documentReference = firestore.collection("QUIZ1").document("Level");

        Map<String,Object> levData = new ArrayMap<>();
        levData.put("NAME" , title);
        levData.put("NUMSUB", 0);

        firestore.collection("QUIZ1").document("LEV" + (levList.size() + 1))
                        .set(levData)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {

                                Map<String,Object> levDoc = new ArrayMap<>();
                                levDoc.put("LEV" + (levList.size() + 1) , title);
                                levDoc.put("Count" , (levList.size() + 1));

                                firestore.collection("QUIZ1").document("Level")
                                        .update(levDoc)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {

                                                Toast.makeText(QuizLevAdmin.this, "Level added successfully",Toast.LENGTH_SHORT).show();

                                                levList.add(new LevelModel(title, "0"));
                                                adapter.notifyItemInserted(levList.size());

                                                loadingDialog.dismiss();

                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {

                                                Toast.makeText(QuizLevAdmin.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                                                loadingDialog.dismiss();

                                            }
                                        });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(QuizLevAdmin.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                        loadingDialog.dismiss();

                    }
                });


    }

}