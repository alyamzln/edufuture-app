package com.example.quizsection;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsAdmin extends AppCompatActivity implements View.OnClickListener{

    private Button logout;
    private ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_admin);

        logout = (Button) findViewById(R.id.logout_admin);
        back = findViewById(R.id.back_btn);

        logout.setOnClickListener((View.OnClickListener) this);
        back.setOnClickListener((View.OnClickListener) this);

    }

    @Override
    public void onClick(View view) {

        Intent i;

        switch(view.getId()){
            case R.id.back_btn:
                i = new Intent(this, PageNavigationAdmin.class);
                startActivity(i);
                break;

            case R.id.logout_admin:
                Toast.makeText(SettingsAdmin.this,"Logout Successfully",Toast.LENGTH_SHORT).show();
                i = new Intent(this, MainActivity.class);
                startActivity(i);
                break;
        }

    }
}