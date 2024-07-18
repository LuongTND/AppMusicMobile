package com.example.echobeat.modelSqlite;

public class ListPlaylist {
    private int plSongId;

    private String plSongUrl;
    private String plTitle;

    private String plPictureSong;

    public ListPlaylist(int plSongId, String plSongUrl, String plTitle, String plPictureSong) {
        this.plSongId = plSongId;
        this.plSongUrl = plSongUrl;
        this.plTitle = plTitle;
        this.plPictureSong = plPictureSong;
    }

    public ListPlaylist() {
    }

    public int getPlSongId() {
        return plSongId;
    }

    public void setPlSongId(int plSongId) {
        this.plSongId = plSongId;
    }

    public String getPlSongUrl() {
        return plSongUrl;
    }

    public void setPlSongUrl(String plSongUrl) {
        this.plSongUrl = plSongUrl;
    }

    public String getPlTitle() {
        return plTitle;
    }

    public void setPlTitle(String plTitle) {
        this.plTitle = plTitle;
    }

    public String getPlPictureSong() {
        return plPictureSong;
    }

    public void setPlPictureSong(String plPictureSong) {
        this.plPictureSong = plPictureSong;
    }
}
