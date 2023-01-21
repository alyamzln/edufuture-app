package com.example.quizsection;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class AdapterVideo extends RecyclerView.Adapter<AdapterVideo.HolderView> {

    private Context context;

    private ArrayList<Member> videoArrayList;

    public AdapterVideo(Context context, ArrayList<Member> videoArrayList) {
        this.context = context;
        this.videoArrayList = videoArrayList;
    }

    @NonNull
    @Override
    public HolderView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // inflate layout item.xml
        //View view= LayoutInflater.from(context).inflate(R.layout.item, parent, false);
        View view= LayoutInflater.from(context).inflate(R.layout.list_video_lyt, parent, false);
        // TO DO: Refer Adapter kat Courses Page
        return new HolderView(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderView holder, int position) {
        // Get, format, set data, handle clicks

        // Get Data
        Member member=videoArrayList.get(position);

        String id= member.getId();
        String title= member.getTitle();
        String timestamp= member.getTimestamp();
        String videoUrl= member.getVideoUrl();

        // format timestamp no need

        // set data
        holder.textView.setText(title);
        holder.videoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setVideoUrl(member, holder);
            }
        });

        // handle click for delete video
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Delete").setMessage("Are you sure you want to delete video: "+ title)
                                .setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        deleteVideo(member);
                                    }
                                }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        }).show();

            }
        });
    }

    private void setVideoUrl(Member member, HolderView holder) {
        // show progress
        //holder.progressBar.setVisibility(View.VISIBLE);

        // get video url
        String videoUrl = member.getVideoUrl();
        MediaController mediaController=new MediaController(context);
        mediaController.setAnchorView(holder.videoView);

        Uri videoUri  = Uri.parse(videoUrl);
        holder.videoView.setMediaController(mediaController);
        holder.videoView.setVideoURI(videoUri);

        holder.videoView.requestFocus();
        holder.videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                // video is ready to play
                mediaPlayer.start();
                //mediaPlayer.pause();
            }
        });

        holder.videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                // restart video if completed
                mediaPlayer.start();
            }
        });
    }

    private void deleteVideo(Member member) {
        String videoId=member.getId();
        String videoUrl= member.getVideoUrl();
        String subjectName= member.getSubject();

        // Delete from firebase storage
        StorageReference reference = FirebaseStorage.getInstance().getReferenceFromUrl(videoUrl);
        reference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // delete from firebase storage

                // delete from firebase database
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(subjectName);
                databaseReference.child(videoId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // delete from firebase database
                        Toast.makeText(context, "Video Deleted Successfully",Toast.LENGTH_SHORT).show();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, ""+e.getMessage(),Toast.LENGTH_SHORT).show();

                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context,""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        // return size of the list
        return videoArrayList.size();
    }

    class HolderView extends RecyclerView.ViewHolder{
        // UI Views of item.xml
        VideoView videoView;
        TextView textView;
        ImageView imageView;

        public HolderView(@NonNull View itemView) {
            super(itemView);
            videoView=itemView.findViewById(R.id.video);
            textView=itemView.findViewById(R.id.vid_item);
            imageView=itemView.findViewById(R.id.deleteVideo);
        }
    }
}
