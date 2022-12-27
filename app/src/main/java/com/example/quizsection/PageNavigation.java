package com.example.quizsection;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

//create three cardviews that link to My Recipes, Recipes For You and Logout
public class PageNavigation extends AppCompatActivity implements View.OnClickListener{

    private CardView courses, quiz, studyRoom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page_navigation);

        courses = (CardView) findViewById(R.id.cardview_courses);
        quiz = (CardView) findViewById(R.id.cardview_quiz);
        studyRoom = (CardView) findViewById(R.id.cardview_room);
        courses.setOnClickListener((View.OnClickListener) this);
        quiz.setOnClickListener((View.OnClickListener) this);
        studyRoom.setOnClickListener((View.OnClickListener) this);

    }

    @Override
    public void onClick(View v) {

        Intent i;
        switch (v.getId()) {
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
