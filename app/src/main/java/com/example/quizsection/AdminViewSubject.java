package com.example.quizsection;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class AdminViewSubject extends AppCompatActivity {

    Intent data;
    TextView subjectName;
    ImageView backButton;

    String subject;

    private View backDrop;
    private boolean rotate = false;
    private View lytUpload, lytDelete;

    RecyclerView recyclerView;

    FirebaseFirestore firebaseFirestore;
    FirebaseDatabase firebaseDatabase;

    // For AdapterVideo.java and item.xml
    private RecyclerView videosRv;
    private ArrayList<Member> videoArrayList;
    private AdapterVideo adapterVideo;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_view_subject);

        subjectName=findViewById(R.id.subj);
        backButton=findViewById(R.id.backArrow);
        videosRv=findViewById(R.id.showVideo);

        firebaseFirestore= FirebaseFirestore.getInstance();

        data=getIntent();

        backDrop = findViewById(R.id.back);
        final FloatingActionButton fabUpload = findViewById(R.id.fab_upload);
        final FloatingActionButton fabDelete = findViewById(R.id.fab_delete);
        final FloatingActionButton fabAdd = findViewById(R.id.fab_add);

        lytUpload = findViewById(R.id.lyt_upload);
        lytDelete = findViewById(R.id.lyt_delete);

        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleFabMode(view);
            }
        });

        backDrop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleFabMode(fabAdd);
            }
        });

        // Display the subject name at the top page
        subject = data.getStringExtra("subject");
        subjectName.setText(subject);

        // TO DO: Add recycler view to show video
        loadVideoFromFirebase();

        fabUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Upload Video", Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(AdminViewSubject.this,UploadVideo.class);
                // Pass subject name to UploadVideo.java
                intent.putExtra("name",subject);
                startActivity(intent);
                finish();
            }
        });

        fabDelete.setOnClickListener(new View.OnClickListener() {
            String subj=subject+"/";
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(AdminViewSubject.this);
                builder.setTitle("Delete").setMessage("Are you sure you want to delete this subject?")
                        .setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // Delete all videos from Realtime Database
                                DatabaseReference reference1=FirebaseDatabase.getInstance().getReference(subject);
                                reference1.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot1) {
                                        for (DataSnapshot ds: snapshot1.getChildren()){
                                            reference1.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {

                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {

                                                }
                                            });
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                                // Delete the respective subject from Firestore Database
                                DocumentReference documentReference=firebaseFirestore.collection("subjects").document(subject);
                                documentReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                    }
                                });
                                //Delete all videos stored in Firebase Storage
                                StorageReference reference = FirebaseStorage.getInstance().getReference(subject);
                                reference.listAll()
                                        .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                                            @Override
                                            public void onSuccess(ListResult listResult) {
                                                List<StorageReference> srList=listResult.getItems();

                                                for (StorageReference sr:srList){
                                                    sr.delete();
                                                }
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {

                                            }
                                        });
                            }
                        }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        }).show();

            }
        });


        // Direct admin to the course page
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(AdminViewSubject.this,AdminCoursePage.class);
                startActivity(intent);
                finish();
            }
        });


    }

    private void loadVideoFromFirebase() {
        //init (initialize) array list before adding it
        videoArrayList=new ArrayList<>();

        //db reference
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference(subject);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // clear list before adding data into it
                videoArrayList.clear();
                for (DataSnapshot ds: snapshot.getChildren()){
                    //get Data
                    Member member=ds.getValue(Member.class);
                    // add model/data into list
                    videoArrayList.add(member);
                }
                //setup adapter
                adapterVideo = new AdapterVideo(AdminViewSubject.this, videoArrayList);
                videosRv.setAdapter(adapterVideo);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    // Function for extended floating action bar
    private void toggleFabMode(View v) {
        rotate = ViewAnimation.rotateFab(v, !rotate);
        if (rotate){
            ViewAnimation.ShowIn(lytUpload);
            ViewAnimation.ShowIn(lytDelete);
            backDrop.setVisibility(View.VISIBLE);
        } else {
            ViewAnimation.showOut(lytUpload);
            ViewAnimation.showOut(lytDelete);
            backDrop.setVisibility(View.GONE);
        }
    }
}
