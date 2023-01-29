package com.example.quizsection;

import static com.example.quizsection.QuizChapters.subj_id;
import static com.example.quizsection.QuizSubjects.level_id;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class QuizQuestions extends AppCompatActivity implements View.OnClickListener {

    private TextView quesCount, question, timer;
    private Button option1, option2, option3, option4;
    private List<Question> questionList;
    private int quesNum;
    private CountDownTimer countdown;
    private int score;
    private FirebaseFirestore firestore;
    public static int chap_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_questions);

        quesCount = findViewById(R.id.QuestionNumber);
        question = findViewById(R.id.question);
        timer = findViewById(R.id.countdown);

        option1 = findViewById(R.id.option1);
        option2 = findViewById(R.id.option2);
        option3 = findViewById(R.id.option3);
        option4 = findViewById(R.id.option4);

        option1.setOnClickListener(this);
        option2.setOnClickListener(this);
        option3.setOnClickListener(this);
        option4.setOnClickListener(this);

        chap_id = getIntent().getIntExtra("CHAP_ID", 1);
        firestore = FirebaseFirestore.getInstance();

        getQuestionsList();

        score = 0;
    }

    private void getQuestionsList()
    {
        questionList = new ArrayList<>();

         firestore.collection("QUIZ1").document("LEV" + String.valueOf(level_id))
                         .collection("SUB" + String.valueOf(subj_id)).document("CHAPTERS")
                         .collection("CHAP" + String.valueOf(chap_id))
                         .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                     @Override
                     public void onComplete(@NonNull Task<QuerySnapshot> task) {

                         if(task.isSuccessful())
                         {
                             QuerySnapshot questions = task.getResult();

                             for(QueryDocumentSnapshot doc: questions)
                             {
                                 questionList.add(new Question(doc.getString("QUESTION"),
                                         doc.getString("A"),
                                         doc.getString("B"),
                                         doc.getString("C"),
                                         doc.getString("D"),
                                         Integer.valueOf(doc.getString("ANSWER"))
                                 ));
                             }

                             setQuestion();

                         }
                         else
                         {
                             Toast.makeText(QuizQuestions.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                         }
                     }
                 });

    }

    private void setQuestion()
    {
        timer.setText(String.valueOf(10));

        question.setText(questionList.get(0).getQuestion());
        option1.setText(questionList.get(0).getOptionA());
        option2.setText(questionList.get(0).getOptionB());
        option3.setText(questionList.get(0).getOptionC());
        option4.setText(questionList.get(0).getOptionD());

        quesCount.setText("Question " + String.valueOf(1) + " of " + String.valueOf(questionList.size()));
        startTimer();

        quesNum = 0;

    }

    private void startTimer()
    {
        countdown = new CountDownTimer(12000, 1000) {
            @Override
            public void onTick(long l) {
                if(l < 10000)
                    timer.setText(String.valueOf(l / 1000));
            }

            @Override
            public void onFinish() {
                changeQuestion();
            }
        };

        countdown.start();
    }

    @Override
    public void onClick(View view) {

        int selectedOption = 0;

        switch (view.getId())
        {
            case R.id.option1:
                selectedOption = 1;
                break;

            case R.id.option2:
                selectedOption = 2;
                break;

            case R.id.option3:
                selectedOption = 3;
                break;

            case R.id.option4:
                selectedOption = 4;
                break;
        }

        countdown.cancel();
        checkAnswer(selectedOption , view);

    }

    private void checkAnswer (int selectedOption, View view)
    {
        if(selectedOption == questionList.get(quesNum).getCorrectAns())
        {
            //Correct answer
            ((Button)view).setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
            score++;
        }
        else
        {
            //Wrong answer
            ((Button)view).setBackgroundTintList(ColorStateList.valueOf(Color.RED));

            switch (questionList.get(quesNum).getCorrectAns())
            {
                case 1:
                    option1.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
                    break;
                case 2:
                    option2.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
                    break;
                case 3:
                    option3.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
                    break;
                case 4:
                    option4.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
                    break;
            }
        }

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                changeQuestion();
            }
        }, 1000);

    }

    private void changeQuestion()
    {
        if(quesNum < questionList.size() - 1)
        {
            quesNum++;

            playAnim(question, 0, 0);
            playAnim(option1, 0, 1);
            playAnim(option2, 0, 2);
            playAnim(option3, 0, 3);
            playAnim(option4, 0, 4);

            quesCount.setText("Question " + String.valueOf(quesNum+1) + " of " + String.valueOf(questionList.size()));

            timer.setText(String.valueOf(10));
            startTimer();
        }
        else
        {
            //Go to score activity
            Intent intent = new Intent(QuizQuestions.this,ScoreActivity.class);
            intent.putExtra("SCORE", "You scored " + String.valueOf(score) + " out of " + String.valueOf(questionList.size()) + "!");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            //QuizQuestions.this.finish();
        }
    }

    private void playAnim(View view, final int value, int viewNum)
    {
        view.animate().alpha(value).scaleX(value).scaleY(value).setDuration(500)
                .setStartDelay(100).setInterpolator(new DecelerateInterpolator())
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {
                        if(value == 0)
                        {
                            switch(viewNum)
                            {
                                case 0:
                                    ((TextView)view).setText(questionList.get(quesNum).getQuestion());
                                    break;

                                case 1:
                                    ((Button)view).setText(questionList.get(quesNum).getOptionA());
                                    break;

                                case 2:
                                    ((Button)view).setText(questionList.get(quesNum).getOptionB());
                                    break;

                                case 3:
                                    ((Button)view).setText(questionList.get(quesNum).getOptionC());
                                    break;

                                case 4:
                                    ((Button)view).setText(questionList.get(quesNum).getOptionD());
                                    break;
                            }

                            if(viewNum != 0)
                            {
                                ((Button)view).setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFFFFF")));
                            }

                            playAnim(view, 1, viewNum);
                        }
                    }

                    @Override
                    public void onAnimationCancel(Animator animator) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animator) {

                    }
                });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        countdown.cancel();
    }
}