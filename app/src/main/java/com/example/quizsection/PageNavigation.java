package com.example.quizsection;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

public class PageNavigation extends AppCompatActivity implements View.OnClickListener{

    private CardView courses, quiz, studyRoom;
    private ImageView settings, profilIcon;
    private TextView name;

    FirebaseAuth auth = FirebaseAuth.getInstance();
    String usernameID = auth.getCurrentUser().getUid();
    FirebaseFirestore fstore = FirebaseFirestore.getInstance();

    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page_navigation);

        courses =  findViewById(R.id.cardview_courses);
        quiz =  findViewById(R.id.cardview_quiz);
        studyRoom = findViewById(R.id.cardview_room);
        settings = (ImageView) findViewById(R.id.settings_user);
        name = findViewById(R.id.display_name);
        profilIcon = findViewById(R.id.profile_icon);

        courses.setOnClickListener((View.OnClickListener) this);
        quiz.setOnClickListener((View.OnClickListener) this);
        studyRoom.setOnClickListener((View.OnClickListener) this);
        settings.setOnClickListener((View.OnClickListener) this);

        DocumentReference documentReference = fstore.collection("users").document(usernameID);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                name.setText(value.getString("fName") +" " + value.getString("lName"));
            }
        });


        storageReference = FirebaseStorage.getInstance().getReference().child("images/" + usernameID +"/" + usernameID);
        try{
            final File localFile = File.createTempFile(usernameID, "jpg");
            storageReference.getFile(localFile)
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                            profilIcon.setImageBitmap(bitmap);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(PageNavigation.this, "Error occurred", Toast.LENGTH_LONG).show();
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        } ;

        profilIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(PageNavigation.this);
                builder.setTitle("Logout").setMessage("Are you sure you want to logout")
                        .setPositiveButton("LOGOUT", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                auth.signOut();
                                Intent intent = new Intent(PageNavigation.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                                Toast.makeText(PageNavigation.this, "Logout Successfully", Toast.LENGTH_SHORT).show();
                            }
                        }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        }).show();
            }
        });
    }

    @Override
    public void onClick(View v) {

        Intent i;
        switch (v.getId()) {
            case R.id.settings_user:
                i = new Intent(PageNavigation.this, Settings.class);
                startActivity(i);
                finish();
                break;

            case R.id.cardview_courses:
                i = new Intent(this, CoursesPage.class);
                startActivity(i);
                break;
            case R.id.cardview_quiz:
                i = new Intent(this, QuizLevel.class);
                startActivity(i);
                break;

            case R.id.cardview_room:
                i = new Intent(this, StudyRoom.class);
                startActivity(i);
                break;

        }

    }

}
