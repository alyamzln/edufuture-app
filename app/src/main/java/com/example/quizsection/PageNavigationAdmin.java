package com.example.quizsection;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class PageNavigationAdmin extends AppCompatActivity implements View.OnClickListener{

    private Button courses, quiz, studyRoom, report;
    private ImageView menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page_navigation_admin);

        courses = (Button) findViewById(R.id.bcourses_edit_btn);
        quiz = (Button) findViewById(R.id.quiz_edit_btn);
        studyRoom = (Button) findViewById(R.id.eduroom_edit_btn);
        report =  (Button) findViewById(R.id.report_edit_btn);
        menu = (ImageView) findViewById(R.id.menu_admin);

        courses.setOnClickListener((View.OnClickListener) this);
        quiz.setOnClickListener((View.OnClickListener) this);
        studyRoom.setOnClickListener((View.OnClickListener) this);
        report.setOnClickListener((View.OnClickListener) this);
        menu.setOnClickListener((View.OnClickListener) this);
    }

    @Override
    public void onClick(View view) {

        Intent intent;

        switch(view.getId()){
            case R.id.bcourses_edit_btn:
                intent = new Intent(this, AdminCoursePage.class);
                startActivity(intent);
                break;
        }

    }
}