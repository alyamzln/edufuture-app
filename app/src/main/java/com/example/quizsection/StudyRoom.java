package com.example.quizsection;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class StudyRoom extends AppCompatActivity {

    private ImageView back;
    Button createBtn,joinBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_room);
        getSupportActionBar().setTitle("EduRoom");

        back = findViewById(R.id.back_btn_room);
        createBtn = findViewById(R.id.createBtn);
        joinBtn = findViewById(R.id.joinBtn);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(StudyRoom.this, PageNavigation.class);
                startActivity(i);
                finish();
            }
        });

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