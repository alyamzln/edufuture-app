package com.example.quizsection;

import static com.example.quizsection.QuizChapters.subj_id;
import static com.example.quizsection.QuizSubjects.level_id;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Report extends AppCompatActivity {

    String[] items ={"Courses", "Quizzes", "EduRoom"};
    private ImageView back;
    private TextView textViewData;

    AutoCompleteTextView autoCompleteTxt;

    ArrayAdapter<String > adapterItems;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        autoCompleteTxt = findViewById(R.id.auto_complete_txt);
        back = findViewById(R.id.back_btn1);

        textViewData = findViewById(R.id.text_view_data);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Report.this, PageNavigationAdmin.class);
                startActivity(i);
                finish();
            }
        });
        adapterItems = new ArrayAdapter<>(this,R.layout.list_item, items );
        autoCompleteTxt.setAdapter(adapterItems);

        autoCompleteTxt.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String item = parent.getItemAtPosition(position).toString();
                if (item.equals("Courses"))
                    adminCourse();
                else if (item.equals("Quizzes"))
                    adminQuiz();
                else if (item.equals("EduRoom"))
                    adminRoom();
//                else if (item.equals("User History"))
//                    adminLog();
            }
        });
    }

    private void adminCourse(){
       // Toast.makeText(getApplicationContext(), "Courses", Toast.LENGTH_SHORT).show();
        //id, date, subject open
        db.collection("reports").document("courses").collection("courses")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            String data = "", temp = "";
                            String[] strArr = null;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                temp = document.getId() + "\n";
                                temp += document.getData();
                                //           temp += String.valueOf(document.getData());
                                // strArr = temp.split(",");
                                data += temp + "\n";

                                data += "\n";
                            }
                            textViewData.setText(data);
                        }
                    }
                });
    }
    private void adminQuiz(){
      //  Toast.makeText(getApplicationContext(), "Quizzes", Toast.LENGTH_SHORT).show();
        //id, date, level, subject, chapter, score
        db.collection("reports").document("quizzes").collection("quizzes")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            String data = "", temp = "";
                            String[] strArr = null;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                temp = document.getId() + "\n";
                                temp += document.getData();
                                //           temp += String.valueOf(document.getData());
                                // strArr = temp.split(",");
                                data += temp + "\n";

                                data += "\n";
                            }
                            textViewData.setText(data);
                        }
                    }
                });
    }
    private void adminRoom(){
     //   Toast.makeText(getApplicationContext(), "room", Toast.LENGTH_SHORT).show();
        //id, date, room code
        db.collection("reports").document("rooms").collection("rooms")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            String data = "", temp = "";
                            String[] strArr = null;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                temp = document.getId() + "\n";
                                temp += document.getData();
                     //           temp += String.valueOf(document.getData());
                               // strArr = temp.split(",");
                                data += temp + "\n";

                                data += "\n";
                            }
                            textViewData.setText(data);
                        }
                    }
                });
    }
    private void adminLog(){
     //   Toast.makeText(getApplicationContext(), "log", Toast.LENGTH_SHORT).show();
        //id date activity

        db.collection("users")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            String[] active_users = null;
                            String temp ="";
                            for (QueryDocumentSnapshot documentSnapshot: task.getResult()) {
                                temp += documentSnapshot.getId() +"\n";
                            }

                            active_users = temp.split("\n");

                            for (int i=0; i<active_users.length; i++){
                                String uID = active_users[i];
                                db.collection("reports").document("users activity").collection(uID)
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    String data = "User ID: " + uID +"\n" , temp = "";
                                                    String[] strArr = null;
                                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                                        temp = document.getId() + "\n";
                                                        temp += document.getData();
                                                        //           temp += String.valueOf(document.getData());
                                                        // strArr = temp.split(",");
                                                        data += temp + "\n";
                                                    }
                                                    textViewData.setText(data);
                                                }
                                            }
                                        });
                            }

                        }
                    }
                });


        //        db.collection("users")
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()) {
//                            String[] allUsers = null;
//                            int totUser = 0;
//                          //  List<String> allUsers = new ArrayList<String>();
//                            for (QueryDocumentSnapshot document : task.getResult()) {
//                                Log.d(TAG, document.getId() + " => " + document.getData());
//                                allUsers[totUser] = document.getId() + "\n";
//                                totUser++;
//                            }
//                        }
//                    }
//                });
    }
}