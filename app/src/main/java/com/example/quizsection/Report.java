package com.example.quizsection;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Report extends AppCompatActivity {

    String[] items ={"Courses", "Quizzes", "EduRoom", "User History"};
    private ImageView back;
    private TextView textViewData;

    AutoCompleteTextView autoCompleteTxt;

    ArrayAdapter<String > adapterItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        autoCompleteTxt = findViewById(R.id.auto_complete_txt);
        back = findViewById(R.id.back_btn1);

        textViewData = findViewById(R.id.text_view_data);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Report.this, PageNavigationAdmin.class);
                startActivity(i);
                finish();
            }
        });
        adapterItems = new ArrayAdapter<>(this,R.layout.list_item, items );
        autoCompleteTxt.setAdapter(adapterItems);

        autoCompleteTxt.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String item = parent.getItemAtPosition(position).toString();
                if (item.equals("Courses"))
                    adminCourse();
                else if (item.equals("Quizzes"))
                    adminQuiz();
                else if (item.equals("EduRoom"))
                    adminRoom();
                else if (item.equals("User History"))
                    adminLog();
            }
        });
    }

    private void adminCourse(){
       // Toast.makeText(getApplicationContext(), "Courses", Toast.LENGTH_SHORT).show();
        //id, date, subject open
        textViewData.setText("User ID: 9CAcWFndgEhEw1Ka6yFwNKqyVYv1\n" +
                "Date: Jan 27, 2023\n" +
                "Subject: Math\n\n" +
                "User ID: FYSzlehaH8ZEvG3YsfQg5b0xl042\n" +
                "Date: Jan 26, 2023\n" +
                "Subject: History");
    }
    private void adminQuiz(){
      //  Toast.makeText(getApplicationContext(), "Quizzes", Toast.LENGTH_SHORT).show();
        //id, date, level, subject, chapter, score
        for (int i=0; i<100; i++) {
            textViewData.setText("User ID: 9CAcWFndgEhEw1Ka6yFwNKqyVYv1\n" +
                    "Date: Jan 27, 2023\n" +
                    "Quiz: FORM 1\\HISTORY\\CHAPTER 1\n" +
                    "Score: 3/3\n\n" +
                    "User ID: FYSzlehaH8ZEvG3YsfQg5b0xl042" +
                    "Date: Jan 27, 2023\n" +
                    "Quiz: FORM 1\\HISTORY\\CHAPTER 1\n" +
                    "Score: 3/3\n\n");
        }
    }
    private void adminRoom(){
     //   Toast.makeText(getApplicationContext(), "room", Toast.LENGTH_SHORT).show();
        //id, date, room code
        textViewData.setText("User ID: 9CAcWFndgEhEw1Ka6yFwNKqyVYv1\n" +
                "Date: Jan 27, 2023\n" +
                "Room Code: abcABC\n\n" +
                "User ID: 9CAcWFndgEhEw1Ka6yFwNKqyVYv1\n" +
                "Date: Jan 27, 2023\n" +
                "Room Code: bilik3A");
    }
    private void adminLog(){
     //   Toast.makeText(getApplicationContext(), "log", Toast.LENGTH_SHORT).show();
        //id date activity
        textViewData.setText("User ID: 9CAcWFndgEhEw1Ka6yFwNKqyVYv1\n" +
                "Browse Course Math\tJan 27, 2023\n" +
                "Take Quiz FORM 1\\HISTORY\\CHAPTER 1\tJan 27, 2023\n" +
                "Create Room\t\tJan 27, 2023\n" +
                "Create Room\t\tJan 27, 2023\n\n" +
                "User ID: FYSzlehaH8ZEvG3YsfQg5b0xl042\n" +
                "Browse Course History\tJan 26, 2023\n" +
                "Take Quiz FORM 1\\HISTORY\\CHAPTER 1\tJan 27, 2023\n");
    }
}