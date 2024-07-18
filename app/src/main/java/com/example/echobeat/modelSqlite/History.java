package com.example.echobeat.modelSqlite;

public class History {
    private int historyId;
    private int songId;   // This should reference the Song class in your application
    private String userId;   // This should reference the User class in your application

    public History() {
    }

    public History(int historyId, int songId, String userId) {
        this.historyId = historyId;
        this.songId = songId;
        this.userId = userId;
    }

    public int getHistoryId() {
        return historyId;
    }

    public void setHistoryId(int historyId) {
        this.historyId = historyId;
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

    @Override
    public String toString() {
        return "History{" +
                "historyId=" + historyId +
                ", songId=" + songId +
                ", userId='" + userId + '\'' +
                '}';
    }
}
