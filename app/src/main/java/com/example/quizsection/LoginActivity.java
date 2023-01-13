package com.example.quizsection;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private EditText emailEt,passwordEt;
    private Button SignInButton;
    private TextView SignUpTv;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    @Override //retrieve user email and password from firebase
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        firebaseAuth=FirebaseAuth.getInstance();
        emailEt=findViewById(R.id.email);
        passwordEt=findViewById(R.id.password);
        SignInButton=findViewById(R.id.login);
        progressDialog=new ProgressDialog(this);
        SignUpTv=findViewById(R.id.signUpTv);
        SignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Login();
            }
        }); //direct first-time users to sign up page
        SignUpTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
                finish();
            }
        });
    } //login verification
    private void Login(){
        String email=emailEt.getText().toString();
        String password=passwordEt.getText().toString();
        if(TextUtils.isEmpty(email)){
            emailEt.setError("Enter your email");
            return;
        }
        else if(TextUtils.isEmpty(password)){
            passwordEt.setError("Enter your password");
            return;
        }
        progressDialog.setTitle("Please Wait");
        progressDialog.setMessage("Just a moment...");
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(false);
        firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){ //function if user input match firebase records

                    Toast.makeText(LoginActivity.this,"Login Successfully",Toast.LENGTH_LONG).show();
                    Intent intent=new Intent(LoginActivity.this, PageNavigation.class);
                    startActivity(intent);
                    finish();
                }
                else{ //function if user input do not match firebase records
                    Toast.makeText(LoginActivity.this,"Sign In fail!",Toast.LENGTH_LONG).show();
                }
                progressDialog.dismiss();
            }
        });
    }

}