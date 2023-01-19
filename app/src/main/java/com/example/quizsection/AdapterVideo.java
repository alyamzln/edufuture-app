package com.example.quizsection;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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
        //setVideoUrl(member, holder);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // comment this first
                //Intent intent=new Intent(view.getContext(), WatchVideo.class);
                //intent.putExtra("title",member.getTitle());
                //view.getContext().startActivity(intent);
            }
        });
    }

//    private void setVideoUrl(Member member, HolderView holder) {
//        // show progress
//        holder.progressBar.setVisibility(View.VISIBLE);
//
//        // get video url
//        String videoUrl = member.getVideoUrl();
//        MediaController mediaController=new MediaController(context);
//        mediaController.setAnchorView(holder.videoView);
//
//        Uri videoUri  = Uri.parse(videoUrl);
//        holder.videoView.setMediaController(mediaController);
//        holder.videoView.setVideoURI(videoUri);
//
//        holder.videoView.requestFocus();
//        holder.videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//            @Override
//            public void onPrepared(MediaPlayer mediaPlayer) {
//                // video is ready to play
//                mediaPlayer.start();
//            }
//        });
//
//        holder.videoView.setOnInfoListener(new MediaPlayer.OnInfoListener() {
//            @Override
//            public boolean onInfo(MediaPlayer mp, int what, int extra) {
//                // to check if buffering, rendering, etc
//                switch (what){
//                    case MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
//                    case MediaPlayer.MEDIA_INFO_BUFFERING_START: {
//                        // rendering  and buffering started
//                        holder.progressBar.setVisibility(View.VISIBLE);
//                        return true;
//                    }
//                    case MediaPlayer.MEDIA_INFO_BUFFERING_END: {
//                        // Buffering ended
//                        holder.progressBar.setVisibility(View.GONE);
//                        return true;
//                    }
//                }
//                return false;
//            }
//        });
//
//        holder.videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//            @Override
//            public void onCompletion(MediaPlayer mediaPlayer) {
//                // restart video if completed
//                mediaPlayer.start();
//            }
//        });
//    }

    @Override
    public int getItemCount() {
        // return size of the list

        // TO DO: fix this line ada problem
        return videoArrayList.size();
    }

    class HolderView extends RecyclerView.ViewHolder{
        // UI Views of item.xml
        //VideoView videoView;
        TextView textView;
        //ProgressBar progressBar;

        public HolderView(@NonNull View itemView) {
            super(itemView);
            //videoView=itemView.findViewById(R.id.video);
            textView=itemView.findViewById(R.id.vid_item);
            // progressBar=itemView.findViewById(R.id.progressBar);
        }
    }
}
