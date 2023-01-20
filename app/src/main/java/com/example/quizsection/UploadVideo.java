package com.example.quizsection;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

import io.reactivex.rxjava3.annotations.Nullable;

public class UploadVideo extends AppCompatActivity {

    Intent data;

    private ActionBar actionBar;
    private TextView textView;
    private EditText editText;
    private VideoView videoView;
    private Button uploadVideoButton;
    private FloatingActionButton pickVidFab;
    private ImageView back;

    private String title;
    private String subjName;

    private Uri videoUri = null; // uri of picked video

    private ProgressDialog progressDialog;
    FirebaseFirestore firebaseFirestore;

    // alternate startActivityForResult
    ActivityResultLauncher<Intent> videoActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode() ==Activity.RESULT_OK){
                        Intent data = result.getData();
                        videoUri = data.getData();
                        setVideoToVideoView();
                        // TO DO: kena compress video
                    }
                    else{
                        Toast.makeText(UploadVideo.this, "Unsuccessful", Toast.LENGTH_SHORT).show();
                    }
                }

            });

    @SuppressLint("MissingInflatedId")
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_video);

        firebaseFirestore= FirebaseFirestore.getInstance();

        data=getIntent();

        textView=findViewById(R.id.subjectUpload);
        editText=findViewById(R.id.VideoTitle);
        videoView=findViewById(R.id.videoView);
        uploadVideoButton=findViewById(R.id.buttonUpload);
        pickVidFab=findViewById(R.id.chooseVideo);
        back=findViewById(R.id.back_button1);

        // get subject name from ViewSubject
        Intent iin= getIntent();
        Bundle b = iin.getExtras();
        if(b!=null) {
            subjName =(String) b.get("name");
            textView.setText(subjName);
        }

        // setup progress dialog
        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Please wait...");
        progressDialog.setMessage("Uploading Video");
        progressDialog.setCanceledOnTouchOutside(false);

        // handle click, upload video
        uploadVideoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                title = editText.getText().toString().trim();
                if(TextUtils.isEmpty(title)){
                    Toast.makeText(UploadVideo.this, "Title is required", Toast.LENGTH_SHORT).show();
                }
                else if (videoUri==null){
                    Toast.makeText(UploadVideo.this, "Pick a video", Toast.LENGTH_SHORT).show();
                }else{
                    uploadVideoFirebase();
                }
            }
        });

        // pick video from gallery
        pickVidFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                videoPickDialog();
            }
        });

        // Direct admin to the course page after upload video
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(UploadVideo.this,AdminCoursePage.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void uploadVideoFirebase() {
        progressDialog.show();

        // timestamp
        final String timestamp = ""+System.currentTimeMillis();
        // in firebase storage
        String filePathAndName = subjName + "/" + "video_" + timestamp;
        // storage reference
        StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathAndName);
        // upload video
        storageReference.putFile(videoUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while(!uriTask.isSuccessful());
                Uri downloadUri = uriTask.getResult();
                if (uriTask.isSuccessful()){
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("id", ""+timestamp);
                    hashMap.put("title", ""+title);
                    hashMap.put("timestamp", ""+timestamp);
                    hashMap.put("videoUrl", ""+downloadUri);
                    // Store video with in respective subject folder
                    DatabaseReference reference= FirebaseDatabase.getInstance().getReference(subjName);
                    reference.child(title)
                            .setValue(hashMap)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    // video details added to db
                                    progressDialog.dismiss();
                                    Toast.makeText(UploadVideo.this, "Video Uploaded", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressDialog.dismiss();
                                    Toast.makeText(UploadVideo.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // failed to upload to storage
                progressDialog.dismiss();
                Toast.makeText(UploadVideo.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // prompt user to select video from gallery
    private void videoPickDialog() {
        // options to display in dialog
        String[] options = {"Gallery"};

        // dialog
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("Pick Video From").setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i==0){
                    // gallery
                    videoPickGallery();
                }
            }
        }).show();
    }

    // select the video from gallery
    private void videoPickGallery(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("video/+");
        videoActivityResultLauncher.launch(Intent.createChooser(intent, "Select Videos"));
    }

    // display video on video view
    private void setVideoToVideoView(){
        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);

        //set media controller to video view
        videoView.setMediaController(mediaController);
        // set video Uri
        videoView.setVideoURI(videoUri);
        videoView.requestFocus();
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                videoView.pause();
            }
        });
    }
}
