package com.example.quizsection;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CoursesPage extends AppCompatActivity {
    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    public String currentDateAndTime = sdf.format(new Date());

    TextView textView;
    String uidUser, firstName, emailUser;

    ImageView imageView;
    ImageView logout;
    RecyclerView mrecyclerview;
    StaggeredGridLayoutManager staggeredGridLayoutManager;

    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    StorageReference storageReference;

    FirestoreRecyclerAdapter<firebasemodel,SubjectViewHolder> subjectAdapter;

    @SuppressLint("MissingInflatedId")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courses_page);

        imageView=findViewById(R.id.backArrow);
        textView=findViewById(R.id.welcomeBack1);
        logout=findViewById(R.id.user);

        firebaseFirestore=FirebaseFirestore.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();


        if(firebaseAuth.getCurrentUser()!=null){
            uidUser=firebaseAuth.getCurrentUser().getUid();
            emailUser=firebaseAuth.getCurrentUser().getEmail();
        }
        else{
            Toast.makeText(CoursesPage.this, "Error", Toast.LENGTH_SHORT).show();
        }
        // Get the user's first name to be displayed on Course Page
        firebaseFirestore.collection("users").document(uidUser).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot!=null && documentSnapshot.exists()){
                        firstName=documentSnapshot.getString("fName");
                        textView.setText(firstName+",");
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(CoursesPage.this, "Error", Toast.LENGTH_SHORT).show();
            }
        });

        storageReference = FirebaseStorage.getInstance().getReference().child("images/" + uidUser +"/" + uidUser);
        try{
            final File localFile = File.createTempFile(uidUser, "jpg");
            storageReference.getFile(localFile)
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                            logout.setImageBitmap(bitmap);
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        } ;

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CoursesPage.this, PageNavigation.class);
                startActivity(intent);
                finish();
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(CoursesPage.this);
                builder.setTitle("Logout").setMessage("Are you sure you want to logout")
                        .setPositiveButton("LOGOUT", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                firebaseAuth.signOut();
                                Intent intent = new Intent(CoursesPage.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                                Toast.makeText(CoursesPage.this, "Logout Successfully", Toast.LENGTH_SHORT).show();
                            }
                        }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        }).show();
            }
        });


        //Extract data from Firebase
        Query query=firebaseFirestore.collection("subjects").orderBy("subject",Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<firebasemodel> allusersubjects = new FirestoreRecyclerOptions.Builder<firebasemodel>().setQuery(query,firebasemodel.class).build();

        subjectAdapter = new FirestoreRecyclerAdapter<firebasemodel, SubjectViewHolder>(allusersubjects) {
            @Override
            protected void onBindViewHolder(@NonNull SubjectViewHolder subjectViewHolder, int i, @NonNull firebasemodel firebasemodel) {

                subjectViewHolder.subjectname.setText(firebasemodel.getSubject());
                subjectViewHolder.subjectdescription.setText(firebasemodel.getDescription());

                String docId = subjectAdapter.getSnapshots().getSnapshot(i).getId();

                subjectViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //we have to open subject detail activity
                        Intent intent = new Intent(view.getContext(),ViewSubject.class);
                        intent.putExtra("subject",firebasemodel.getSubject());
                        intent.putExtra("description",firebasemodel.getDescription());
                        intent.putExtra("subjectId",docId);

                        Map<String, Object> data = new HashMap<>();
                        data.put("Browse Course ", firebasemodel.getSubject());
                        firebaseFirestore.collection("reports").document("users activity")
                                .collection(uidUser).document(currentDateAndTime).set(data);

                        Map<String, Object> subj = new HashMap<>();
                        subj.put("User ID", uidUser);
                        subj.put("Subject", firebasemodel.getSubject());
                        firebaseFirestore.collection("reports").document("courses")
                                .collection("courses").document(currentDateAndTime).set(subj);

                        view.getContext().startActivity(intent);
                    }
                });
            }

            @NonNull
            @Override
            public SubjectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.courses_layout,parent,false);
                return new SubjectViewHolder(view);
            }
        };

        mrecyclerview = findViewById(R.id.recyclerview);
        mrecyclerview.setHasFixedSize(true);
        staggeredGridLayoutManager=new StaggeredGridLayoutManager(1,StaggeredGridLayoutManager.VERTICAL);
        mrecyclerview.setLayoutManager(staggeredGridLayoutManager);
        mrecyclerview.setAdapter(subjectAdapter);
    }

    public class SubjectViewHolder extends RecyclerView.ViewHolder
    {
        private TextView subjectname;
        private TextView subjectdescription;
        LinearLayout msubject;

        public SubjectViewHolder(@NonNull View itemView) {
            super(itemView);
            subjectname=itemView.findViewById(R.id.subjectname);
            subjectdescription=itemView.findViewById(R.id.subjectdescription);
            msubject=itemView.findViewById(R.id.subject);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        subjectAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(subjectAdapter!=null)
        {
            subjectAdapter.stopListening();
        }
    }
}
