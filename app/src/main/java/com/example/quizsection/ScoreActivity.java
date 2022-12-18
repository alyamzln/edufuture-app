package com.example.quizsection;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ScoreActivity extends AppCompatActivity {

    private TextView score;
    private Button playAgain, others, homepage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        score = findViewById(R.id.score);
        playAgain = findViewById(R.id.playAgain);
        others = findViewById(R.id.others);
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

        others.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(ScoreActivity.this, PageNavigation.class);
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