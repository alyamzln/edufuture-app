package com.example.quizsection;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class AdminCoursePage extends AppCompatActivity {

    Button newSubjectButton;
    ImageView imageView;
    RecyclerView mrecyclerview;
    StaggeredGridLayoutManager staggeredGridLayoutManager;

    FirebaseFirestore firebaseFirestore;

    FirestoreRecyclerAdapter<firebasemodel,SubjectViewHolder> subjectAdapter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_courses_page);

        newSubjectButton=findViewById(R.id.button2);
        imageView=findViewById(R.id.backArrow);

        firebaseFirestore=FirebaseFirestore.getInstance();

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminCoursePage.this, PageNavigationAdmin.class);
                startActivity(intent);
                finish();
            }
        });

        //Direct admin to AdminNewSubject page
        newSubjectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminCoursePage.this, AdminNewSubject.class);
                startActivity(intent);
                finish();
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
                        Intent intent = new Intent(view.getContext(),AdminViewSubject.class);
                        intent.putExtra("subject",firebasemodel.getSubject());
                        intent.putExtra("description",firebasemodel.getDescription());
                        intent.putExtra("subjectId",docId);

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
