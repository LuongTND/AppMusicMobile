package com.example.echobeat.modelSqlite;

public class Song {
    private int songId;
    private String userId;   // This should reference the User class in your application
    private String songUrl;
    private String title;
    private int duration;
    private int releaseYear;
    private String pictureSong;
    private int categoryId;  // This should reference the Category class in your application

    public Song() {
    }

    public Song(int songId, String userId, String songUrl, String title, int duration, int releaseYear, String pictureSong, int categoryId) {
        this.songId = songId;
        this.userId = userId;
        this.songUrl = songUrl;
        this.title = title;
        this.duration = duration;
        this.releaseYear = releaseYear;
        this.pictureSong = pictureSong;
        this.categoryId = categoryId;
    }

    public int getSongId() {
        return songId;
    }

    public void setSongId(int songId) {
        this.songId = songId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSongUrl() {
        return songUrl;
    }

    public void setSongUrl(String songUrl) {
        this.songUrl = songUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(int releaseYear) {
        this.releaseYear = releaseYear;
    }

    public String getPictureSong() {
        return pictureSong;
    }

    public void setPictureSong(String pictureSong) {
        this.pictureSong = pictureSong;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    @Override
    public String toString() {
        return "Song{" +
                "songId=" + songId +
                ", userId='" + userId + '\'' +
                ", songUrl='" + songUrl + '\'' +
                ", title='" + title + '\'' +
                ", duration=" + duration +
                ", releaseYear=" + releaseYear +
                ", pictureSong='" + pictureSong + '\'' +
                ", categoryId=" + categoryId +
                '}';
    }
}
