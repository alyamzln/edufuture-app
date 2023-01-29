package com.example.quizsection;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

public class QuizReady extends AppCompatActivity {

    private Button startQuiz;
    private Dialog loadingDialog;
    private ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_ready);

        back = findViewById(R.id.back_btn_quiz3);

        loadingDialog = new Dialog(QuizReady.this);
        loadingDialog.setContentView(R.layout.loading_progressbar);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawableResource(R.drawable.progress_background);
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);

        startQuiz = findViewById(R.id.startQuiz);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(QuizReady.this, QuizChapters.class);
                startActivity(i);
                finish();
            }
        });

        startQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                loadingDialog.show();
                Intent intent = new Intent(QuizReady.this, QuizQuestions.class);
                startActivity(intent);
                loadingDialog.dismiss();
            }
        });
    }

}