package com.example.echobeat.modelFirebase;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.Date;

public class History implements Parcelable {
    private String id;
    private String type; // Loại (song, album, playlist)
    private String itemId; // ID của bài hát, album hoặc playlist
    private String title; // Tiêu đề
    private String coverImage; // Resource ID của ảnh bìa
    private Date timestamp; // Thời gian

    public History() {

    }

    public History(String id, String type, String itemId, String title, String coverImage, Date timestamp) {
        this.id = id;
        this.type = type;
        this.itemId = itemId;
        this.title = title;
        this.coverImage = coverImage;
        this.timestamp = timestamp;
    }

    protected History(Parcel in) {
        id = in.readString();
        type = in.readString();
        itemId = in.readString();
        title = in.readString();
        coverImage = in.readString();
        timestamp = new Date(in.readLong());
    }

    public static final Creator<History> CREATOR = new Creator<History>() {
        @Override
        public History createFromParcel(Parcel in) {
            return new History(in);
        }

        @Override
        public History[] newArray(int size) {
            return new History[size];
        }
    };

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(type);
        parcel.writeString(itemId);
        parcel.writeString(title);
        parcel.writeString(coverImage);
        parcel.writeLong(timestamp.getTime());
    }
}
