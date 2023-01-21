package com.example.quizsection;

public class Member {
    String id, subject, title, timestamp, videoUrl;

    public Member() {
    }

    public Member(String id, String subject, String title, String timestamp, String videoUrl) {
        this.id = id;
        this.subject = subject;
        this.title = title;
        this.timestamp = timestamp;
        this.videoUrl = videoUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSubject() {return subject;}

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }
}
