package com.example.echobeat.modelFirebase;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class Song implements Parcelable {
    private String songId;
    private String userId;
    private String songUrl;
    private String title;
    private int duration;
    private Date releaseYear;
    private String pictureSong;
    private String categoryId;
    private int playCount;
    private String albumId;  // Field for album ID

    private String playlistId;

    // Constructors
    public Song() {}

    protected Song(Parcel in) {
        songId = in.readString();
        userId = in.readString();
        songUrl = in.readString();
        title = in.readString();
        duration = in.readInt();
        releaseYear = new Date(in.readLong());  // Read and convert long to Date
        pictureSong = in.readString();
        categoryId = in.readString();
        playCount = in.readInt();
        albumId = in.readString();  // Read album ID
    }

    public Song(String songId, String userId, String songUrl, String title, int duration, Date releaseYear, String pictureSong, String categoryId, int playCount, String albumId) {
        this.songId = songId;
        this.userId = userId;
        this.songUrl = songUrl;
        this.title = title;
        this.duration = duration;
        this.releaseYear = releaseYear;
        this.pictureSong = pictureSong;
        this.categoryId = categoryId;
        this.playCount = playCount;
        this.albumId = albumId;
    }

    // Getters and Setters
    public String getSongId() {
        return songId;
    }

    public void setSongId(String songId) {
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

    public Date getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(Date releaseYear) {
        this.releaseYear = releaseYear;
    }

    public String getPictureSong() {
        return pictureSong;
    }

    public void setPictureSong(String pictureSong) {
        this.pictureSong = pictureSong;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public int getPlayCount() {
        return playCount;
    }

    public void setPlayCount(int playCount) {
        this.playCount = playCount;
    }

    public String getAlbumId() {
        return albumId;
    }

    public void setAlbumId(String albumId) {
        this.albumId = albumId;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(songId);
        dest.writeString(userId);
        dest.writeString(songUrl);
        dest.writeString(title);
        dest.writeInt(duration);
        dest.writeLong(releaseYear != null ? releaseYear.getTime() : -1L);  // Convert Date to long
        dest.writeString(pictureSong);
        dest.writeString(categoryId);
        dest.writeInt(playCount);
        dest.writeString(albumId);  // Write album ID
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Song> CREATOR = new Creator<Song>() {
        @Override
        public Song createFromParcel(Parcel in) {
            return new Song(in);
        }

        @Override
        public Song[] newArray(int size) {
            return new Song[size];
        }
    };
}
