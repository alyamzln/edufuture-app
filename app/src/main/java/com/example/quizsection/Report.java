package com.example.quizsection;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Report extends AppCompatActivity {

    String[] items ={"Courses", "Quizzes", "EduRoom", "User History"};
    private ImageView back;

    AutoCompleteTextView autoCompleteTxt;

    ArrayAdapter<String > adapterItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        autoCompleteTxt = findViewById(R.id.auto_complete_txt);
        back = findViewById(R.id.back_btn1);

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
        Toast.makeText(getApplicationContext(), "Course", Toast.LENGTH_SHORT).show();
    }
    private void adminQuiz(){
        Toast.makeText(getApplicationContext(), "Quiz", Toast.LENGTH_SHORT).show();
    }
    private void adminRoom(){
        Toast.makeText(getApplicationContext(), "room", Toast.LENGTH_SHORT).show();
    }
    private void adminLog(){
        Toast.makeText(getApplicationContext(), "log", Toast.LENGTH_SHORT).show();
    }
}