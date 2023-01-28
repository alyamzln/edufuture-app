package com.example.quizsection;

import static com.example.quizsection.MainActivity.levelList;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class QuizLevel extends AppCompatActivity {

    public static List<String> subjList = new ArrayList<>();
    private ImageView back;

    private GridView level_grid;

    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_level);

        level_grid = findViewById(R.id.levelGridView);
        back = findViewById(R.id.back_btn_quiz);

        LevelAdapter adapter = new LevelAdapter(levelList);
        level_grid.setAdapter(adapter);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(QuizLevel.this, PageNavigation.class);
                startActivity(i);
                finish();
            }
        });

    }

}