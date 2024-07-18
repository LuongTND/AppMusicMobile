package com.example.echobeat.modelSqlite;

public class PlaylistDetail {
    private int playlistId;  // This should reference the Playlist class in your application
    private int songId;      // This should reference the Song class in your application
    private String title;

    public PlaylistDetail() {
    }

    public PlaylistDetail(int playlistId, int songId, String title) {
        this.playlistId = playlistId;
        this.songId = songId;
        this.title = title;
    }

    public int getPlaylistId() {
        return playlistId;
    }

    public void setPlaylistId(int playlistId) {
        this.playlistId = playlistId;
    }

    public int getSongId() {
        return songId;
    }

    public void setSongId(int songId) {
        this.songId = songId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return "PlaylistDetail{" +
                "playlistId=" + playlistId +
                ", songId=" + songId +
                ", title='" + title + '\'' +
                '}';
    }
}
