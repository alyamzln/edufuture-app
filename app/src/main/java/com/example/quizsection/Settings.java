package com.example.quizsection;

import static android.view.View.resolveSize;
import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Settings extends AppCompatActivity implements View.OnClickListener{

    private TextView uname, fname, email, logout, resetPassword, delete;
    private ImageView back, changeIcon, profilePic;
    public Uri imageUri;
    private String newFN = null;
    String[] strA = null;

    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String emailAddress = user.getEmail();
    String usernameID = auth.getCurrentUser().getUid();

    FirebaseFirestore fstore = FirebaseFirestore.getInstance();
    private FirebaseStorage storage;
    StorageReference storageReference;

    DocumentReference documentReference = fstore.collection("users").document(usernameID);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        back = findViewById(R.id.back_btn);
        profilePic = findViewById(R.id.profile_icon);
        changeIcon = findViewById(R.id.change_icon);
        uname = findViewById(R.id.username);
        fname = findViewById(R.id.name);
        email = findViewById(R.id.edit_email);
        resetPassword = (TextView) findViewById(R.id.edit_password);
        logout = (TextView) findViewById(R.id.logout_user);
        delete = (TextView) findViewById(R.id.delete_account);

        uname.setText(usernameID);
        email.setText(emailAddress);

        DocumentReference documentReference = fstore.collection("users").document(usernameID);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                fname.setText(value.getString("fName") +" " + value.getString("lName"));
            }
        });

        storageReference = FirebaseStorage.getInstance().getReference().child("images/" + usernameID +"/" + usernameID);
        try{
            final File localFile = File.createTempFile(usernameID, "jpg");
            storageReference.getFile(localFile)
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                            profilePic.setImageBitmap(bitmap);
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        } ;

        back.setOnClickListener((View.OnClickListener) this);
        profilePic.setOnClickListener((View.OnClickListener) this);
        changeIcon.setOnClickListener((View.OnClickListener) this);
        fname.setOnClickListener((View.OnClickListener) this);
        resetPassword.setOnClickListener((View.OnClickListener) this);
        logout.setOnClickListener((View.OnClickListener) this);
        delete.setOnClickListener((View.OnClickListener) this);

    }

    @Override
    public void onClick(View view) {

        Intent i;

        switch(view.getId()){
            case R.id.back_btn:
                i = new Intent(this, PageNavigation.class);
                startActivity(i);
                finish();
                break;

            case R.id.profile_icon:
                storageReference = FirebaseStorage.getInstance().getReference().child("images/" + usernameID);
                choosePicture();
                break;

            case R.id.change_icon:
                storageReference = FirebaseStorage.getInstance().getReference().child("images/" + usernameID);
                choosePicture();
                break;

            case R.id.name:
                EditText newFname;

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Edit Name");
                builder.setMessage("First name <space> Last name");
                newFname=new EditText(this);
                builder.setView(newFname);

                builder.setPositiveButton("Save Changes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        newFN = newFname.getText().toString();

                        strA = newFN.split(" ");
                        Map<String, Object> u = new HashMap<>();
                        u.put("fName", strA[0]);
                        u.put("lName", strA[1]);

                        documentReference.update(u)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(Settings.this,"Changes Saved",Toast.LENGTH_SHORT).show();
                                        Log.d(TAG, "Sukses");

                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.e(TAG, "onFailure: ",e);
                                    }
                                });
                        fname.setText(newFN);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builder.show();
                break;

            case R.id.edit_password:
                FirebaseAuth.getInstance().sendPasswordResetEmail(emailAddress).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()){
                            Toast.makeText(Settings.this,"Email Sent ",Toast.LENGTH_LONG).show();
                        }
                        else
                            Toast.makeText(Settings.this,"Email Sent Fail",Toast.LENGTH_LONG).show();
                    }
                });
                break;

            case R.id.logout_user:
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(Settings.this,"Logout Successfully",Toast.LENGTH_SHORT).show();
                i = new Intent(this, MainActivity.class);
                startActivity(i);
                break;

            case R.id.delete_account:

                AlertDialog.Builder dialog = new AlertDialog.Builder(Settings.this);
                dialog.setTitle("Are you sure?");
                dialog.setMessage("Deleting this account will result in completely removing your "+
                        "account from the system and you won't be able to access the app.");
                dialog.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    fstore.collection("users").document(usernameID).delete();
                                    Toast.makeText(Settings.this, "Account Deleted", Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(Settings.this, MainActivity.class);
                                    startActivity(intent);
                                } else
                                    Toast.makeText(Settings.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });

                    }
                });

                dialog.show();
                    break;
        }

        }

        private void choosePicture(){
            Intent i = new Intent();
            i.setType("image/*");
            i.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(i, 1);
        }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==1 && resultCode==RESULT_OK && data!=null && data.getData()!=null){
            imageUri = data.getData();
            profilePic.setImageURI(imageUri);
            uploadPicture();
        }

    }

    private void uploadPicture(){

        final ProgressDialog pd = new ProgressDialog(this);
        pd.setTitle("Uploading Image...");
        pd.show();

        StorageReference riversRef = storageReference.child(usernameID);

        riversRef.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        pd.dismiss();
                        Snackbar.make(findViewById(android.R.id.content), "Image Uploaded", Snackbar.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(getApplicationContext(), "Failed to upload", Toast.LENGTH_LONG).show();
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                        double progressPercent = (100.00 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                        pd.setMessage("Percentage: " +(int) progressPercent + "%");
                    }
                });

    }

}
