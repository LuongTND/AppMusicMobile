package com.example.echobeat.modelSqlite;

public class Album {
    private int albumId;
    private String userId;  // This should reference the Artist class in your application
    private int songId;    // This should reference the Song class in your application
    private int releaseYear;

    public Album() {
    }

    public Album(int albumId, String userId, int songId, int releaseYear) {
        this.albumId = albumId;
        this.userId = userId;
        this.songId = songId;
        this.releaseYear = releaseYear;
    }

    public int getAlbumId() {
        return albumId;
    }

    public void setAlbumId(int albumId) {
        this.albumId = albumId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getSongId() {
        return songId;
    }

    public void setSongId(int songId) {
        this.songId = songId;
    }

    public int getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(int releaseYear) {
        this.releaseYear = releaseYear;
    }

    @Override
    public String toString() {
        return "Album{" +
                "albumId=" + albumId +
                ", userId='" + userId + '\'' +
                ", songId=" + songId +
                ", releaseYear=" + releaseYear +
                '}';
    }
}
