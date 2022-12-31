package com.example.quizsection;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class StudyRoom extends AppCompatActivity {

    Button createBtn,joinBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_room);
        getSupportActionBar().setTitle("EduRoom");

        createBtn = findViewById(R.id.createBtn);
        joinBtn = findViewById(R.id.joinBtn);

        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(StudyRoom.this,CreateRoom.class));

            }
        });

        joinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StudyRoom.this,JoinRoom.class));
            }});
        
    }

}