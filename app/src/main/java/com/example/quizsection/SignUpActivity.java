package com.example.quizsection;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {
    public static final String TAG = "TAG";
    private EditText emailEt,passwordEt1,passwordEt2,firstNameEt,lastNameEt, icnum;
    private Button SignUpButton;
    private TextView SignInTv;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore fStore;
    Boolean agree = false;
    String userID;

    String[] state = {"01", "21", "22", "23", "24", "02", "25", "26", "27", "03", "28", "29",
            "04", "30", "05", "31", "59", "06", "32", "33", "07", "34", "35", "08", "36", "37", "38" , "39",
            "09", "40", "10", "41", "42", "43", "44", "11", "45", "46", "12", "47", "48", "49", "13", "50", "51", "52", "53",
            "14", "54", "55", "56", "57", "15", "58", "16", "82"};

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        firebaseAuth=FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        firstNameEt=findViewById(R.id.firstname);
        lastNameEt=findViewById(R.id.lastname);
        emailEt=findViewById(R.id.email);
        passwordEt1=findViewById(R.id.password1);
        passwordEt2=findViewById(R.id.password2);
        icnum = findViewById(R.id.ic_number);

        SignUpButton=findViewById(R.id.register);

        progressDialog=new ProgressDialog(this);
        SignInTv=findViewById(R.id.logInTv);

        SignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Register();
            }
        });
        SignInTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(SignUpActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
    private void Register(){
        String firstname=firstNameEt.getText().toString();
        String lastname=lastNameEt.getText().toString();
        String email=emailEt.getText().toString();
        String password1=passwordEt1.getText().toString();
        String password2=passwordEt2.getText().toString();
        String ic_num=icnum.getText().toString();

        //change ic string to array
        String[] strArray = null;
        strArray = ic_num.split("");

        //to check ic state code
        boolean flag = false;
        String check = (strArray[6]+strArray[7]);
        for (int i=0; i<state.length; i++){
            if(check.equals(state[i]))
                flag = true;
        }

        //to check ic year
        boolean flag1 = false;
        String check1 = (strArray[0]+strArray[1]);
        if (check1.compareTo("04")>0 && check1.compareTo("12")<0)
            flag1 = true;


        if(TextUtils.isEmpty(firstname)){
            firstNameEt.setError("Enter your first name");
            return;
        }
        else if(TextUtils.isEmpty(lastname)){
            lastNameEt.setError("Enter your last name");
            return;
        }
        else if(TextUtils.isEmpty(email)){
            emailEt.setError("Enter your email");
            return;
        }
        else if(TextUtils.isEmpty(password1)){
            passwordEt1.setError("Enter your password");
            return;
        }
        else if(TextUtils.isEmpty(password2)){
            passwordEt2.setError("Confirm your password");
            return;
        }
        else if(TextUtils.isEmpty(ic_num)){
            icnum.setError("Field cannot blank");
            return;
        }
        else if(email.equals("testcat304@gmail.com")){
            emailEt.setError("Email already exist");
            return;
        }


        //password validation
        else if(!password1.equals(password2)){
            passwordEt2.setError("Different password entered");
            return;
        }
        else if(password1.length()<8){
            passwordEt1.setError("Minimum password length should be 8 characters");
            return;
        } //email validation
        else if(!isValidEmail(email)){
            emailEt.setError("Invalid email");
            return;
        }

        else if(ic_num.length() != 12){
            icnum.setError("Invalid input");
            return;
        }

        //if state code is invalid
        else if(flag == false){
            icnum.setError("Invalid state code");
            return;
        }
        //[0,1,2,3,4,5,6,7,8,9,10,11]
        //2005-2011 (12-18 thun) - 05, 06, 07, 08, 09, 10, 11

        // if age is invalid
        else if (flag1 == false){
            icnum.setError("Invalid age");
            return;
        }

        if (!agree)
            termsUse();

        if (agree) {

            progressDialog.setTitle("Creating Account");
            progressDialog.setMessage("We are creating your account...");
            progressDialog.show();
            progressDialog.setCanceledOnTouchOutside(false);
            firebaseAuth.createUserWithEmailAndPassword(email, password1).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) { //function if user is successfully registered
                        Toast.makeText(SignUpActivity.this, "Successfully registered", Toast.LENGTH_LONG).show();
                        userID = firebaseAuth.getCurrentUser().getUid();
                        DocumentReference documentReference = fStore.collection("users").document(userID);
                        Map<String, Object> user = new HashMap<>();
                        user.put("fName", firstname);
                        user.put("lName", lastname);
                        user.put("email", email);
                    //    user.put("password", password1);
                        user.put("ic_number", ic_num);

                        documentReference.set(user).addOnSuccessListener((OnSuccessListener) (aVoid) -> {
                            Log.d(TAG, "onSuccess: user Profile is created for " + userID);

                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, "onFailure: " + e.toString());
                            }
                        });
                        Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else { //function if user is fail to be registered
                        Toast.makeText(SignUpActivity.this, "Sign up fail!" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    progressDialog.dismiss();
                }
            });
        }
    }
    private Boolean isValidEmail(CharSequence target){
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    private void termsUse(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(SignUpActivity.this);
        dialog.setTitle("Agreement to Terms of Use (PLEASE READ!)");
        dialog.setMessage("These Terms and Conditions of Use (the \"Terms of Use\") apply to the EduFuture Application. " +
                "The Application is the property of Universiti Sains Malaysia and its licensors. " +
                "BY USING THE SITE, YOU AGREE TO THESE TERMS OF USE; IF YOU DO NOT AGREE, DO NOT USE THE SITE.\n" +
                "\n" +
                "Your private information such as password and IC Number will not be disclosed to a third party and used for personal gain." +
                "The developer reserves the right, at its sole discretion, to change, modify, add, or remove " +
                "portions of these Terms of Use, at any time. It is your responsibility to check these Terms of Use periodically for changes. " +
                "Your continued use of the app following the posting of changes will mean that you accept and agree to the changes. " +
                "As long as you comply with these Terms of Use, the developer grants you a personal, non-exclusive, non-transferable, " +
                "limited privilege to enter and use the application.");

        dialog.setPositiveButton("I have read and agree to the Terms", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(SignUpActivity.this, "You have agree to the terms of use", Toast.LENGTH_LONG).show();
                agree = true;
            }
        });
        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        dialog.show();
    }
}