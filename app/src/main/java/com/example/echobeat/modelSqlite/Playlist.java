package com.example.echobeat.modelSqlite;

public class Playlist {
    private int playlistId;
    private String userId;  // This should reference the User class in your application
    private String picturePlaylist;
    private String playlistName;

    public Playlist() {
    }

    public Playlist(int playlistId, String userId, String picturePlaylist, String playlistName) {
        this.playlistId = playlistId;
        this.userId = userId;
        this.picturePlaylist = picturePlaylist;
        this.playlistName = playlistName;
    }

    public int getPlaylistId() {
        return playlistId;
    }

    public void setPlaylistId(int playlistId) {
        this.playlistId = playlistId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPicturePlaylist() {
        return picturePlaylist;
    }

    public void setPicturePlaylist(String picturePlaylist) {
        this.picturePlaylist = picturePlaylist;
    }

    public String getPlaylistName() {
        return playlistName;
    }

    public void setPlaylistName(String playlistName) {
        this.playlistName = playlistName;
    }

    @Override
    public String toString() {
        return "Playlist{" +
                "playlistId=" + playlistId +
                ", userId='" + userId + '\'' +
                ", picturePlaylist='" + picturePlaylist + '\'' +
                ", playlistName='" + playlistName + '\'' +
                '}';
    }
}
