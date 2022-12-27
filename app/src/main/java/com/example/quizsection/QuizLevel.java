package com.example.quizsection;

import static com.example.quizsection.MainActivity.levelList;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.GridView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class QuizLevel extends AppCompatActivity {

    public static List<String> subjList = new ArrayList<>();

    private GridView level_grid;

    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_level);

        level_grid = findViewById(R.id.levelGridView);

        LevelAdapter adapter = new LevelAdapter(levelList);
        level_grid.setAdapter(adapter);

    }

}