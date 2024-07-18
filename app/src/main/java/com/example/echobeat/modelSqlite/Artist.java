package com.example.echobeat.modelSqlite;

public class Artist {
    private String userId;
    private String artistName;
    private String biography;

    public Artist() {
    }

    public Artist(String userId, String artistName, String biography) {
        this.userId = userId;
        this.artistName = artistName;
        this.biography = biography;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public String getBiography() {
        return biography;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }

    @Override
    public String toString() {
        return "Artist{" +
                "userId='" + userId + '\'' +
                ", artistName='" + artistName + '\'' +
                ", biography='" + biography + '\'' +
                '}';
    }
}

