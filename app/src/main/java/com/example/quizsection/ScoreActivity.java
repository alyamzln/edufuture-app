package com.example.quizsection;

import static com.example.quizsection.QuizChapters.subj_id;
import static com.example.quizsection.QuizSubjects.level_id;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ScoreActivity extends AppCompatActivity{
    private TextView score;
    private Button playAgain, others, homepage;

    FirebaseAuth auth = FirebaseAuth.getInstance();
    String uidUser = auth.getCurrentUser().getUid();
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        score = findViewById(R.id.score);
        playAgain = findViewById(R.id.playAgain);
        homepage = findViewById(R.id.homepage);

        String score_str = getIntent().getStringExtra("SCORE");
        score.setText(score_str);

        playAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(ScoreActivity.this, QuizQuestions.class);
                ScoreActivity.this.startActivity(intent);
                ScoreActivity.this.finish();
            }
        });

        homepage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(ScoreActivity.this, PageNavigation.class);
                ScoreActivity.this.startActivity(intent);
                ScoreActivity.this.finish();
            }
        });


    }
}