package com.example.quizsection;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
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

//comment
public class SignUpActivity extends AppCompatActivity {
    public static final String TAG = "TAG";
    private EditText emailEt,passwordEt1,passwordEt2,firstNameEt,lastNameEt;
    private Button SignUpButton;
    private TextView SignInTv;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore fStore;
    String userID;

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
        } //password validation
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
        progressDialog.setTitle("Creating Account");
        progressDialog.setMessage("We are creating your account...");
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(false);
        firebaseAuth.createUserWithEmailAndPassword(email,password1).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){ //function if user is successfully registered
                    Toast.makeText(SignUpActivity.this,"Successfully registered",Toast.LENGTH_LONG).show();
                    userID = firebaseAuth.getCurrentUser().getUid();
                    DocumentReference documentReference = fStore.collection ( "users").document(userID);
                    Map<String,Object> user = new HashMap<>();
                    user.put("fName",firstname);
                    user.put("lName",lastname);
                    user.put("email",email);
                    user.put("password",password1);
                    documentReference.set(user).addOnSuccessListener ((OnSuccessListener)(aVoid) -> {
                            Log.d (TAG, "onSuccess: user Profile is created for " + userID);

                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                        public void onFailure(@NonNull Exception e){
                                Log.d (TAG, "onFailure: " + e.toString());
                        }
                    });
                    Intent intent=new Intent(SignUpActivity.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                }
                else{ //function if user is fail to be registered
                    Toast.makeText(SignUpActivity.this,"Sign up fail!" + task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                }
                progressDialog.dismiss();
            }
        });
    }
    private Boolean isValidEmail(CharSequence target){
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }
}