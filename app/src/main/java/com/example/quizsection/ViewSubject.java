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

public class ViewSubject extends AppCompatActivity {

    Intent data;
    TextView subjectName;
    ImageView backButton;

    String subject;

    FirebaseFirestore firebaseFirestore;
    FirebaseDatabase firebaseDatabase;

    // For AdapterVideo.java and item.xml
    private RecyclerView videosRv;
    private ArrayList<Member> videoArrayList;
    private AdapterVideoStudent adapterVideo;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_subject);

        subjectName=findViewById(R.id.subj);
        backButton=findViewById(R.id.backArrow);
        videosRv=findViewById(R.id.showVideo);

        firebaseFirestore= FirebaseFirestore.getInstance();

        data=getIntent();

        // Display the subject name at the top page
        subject = data.getStringExtra("subject");
        subjectName.setText(subject);

        // TO DO: Add recycler view to show video
        loadVideoFromFirebase();

        // Direct admin to the course page
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(ViewSubject.this, CoursesPage.class);
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
                adapterVideo = new AdapterVideoStudent(ViewSubject.this, videoArrayList);
                videosRv.setAdapter(adapterVideo);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}
