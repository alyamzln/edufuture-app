package com.example.quizsection;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import org.jetbrains.annotations.NotNull;

public class ResetPassword extends AppCompatActivity {
    private EditText emailsend;
    private Button login, sendmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        emailsend = findViewById(R.id.email_sent);
        login = findViewById(R.id.back_login);
        sendmail = findViewById(R.id.send_resetpass);

        sendmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String mail = emailsend.getText().toString();
                FirebaseAuth.getInstance().sendPasswordResetEmail(mail).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<Void> task) {

                        if (task.isSuccessful()){
                            Toast.makeText(ResetPassword.this,"Email Sent ",Toast.LENGTH_SHORT).show();
                        }
                        else
                            Toast.makeText(ResetPassword.this,"Email Sent Fail",Toast.LENGTH_SHORT).show();

                    }
                });

            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i;
                i = new Intent(ResetPassword.this, LoginActivity.class);
                startActivity(i);
            }
        });


    }
}