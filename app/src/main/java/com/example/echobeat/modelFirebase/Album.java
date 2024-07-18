package com.example.echobeat.modelFirebase;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.Date;

public class Album implements Parcelable {
    private String albumId;
    private int artistId;
    private String title;
    private Date releaseYear;
    private String coverImage;
    private int genreId;



    // Constructors, getters, and setters
    public Album() {}

    public Album(String albumId, int artistId, String title, Date releaseYear, String coverImage, int genreId) {
        this.albumId = albumId;
        this.artistId = artistId;
        this.title = title;
        this.releaseYear = releaseYear;
        this.coverImage = coverImage;
        this.genreId = genreId;
    }

    protected Album(Parcel in) {
        albumId = in.readString();
        artistId = in.readInt();
        title = in.readString();
        // Đối với kiểu Date, bạn có thể sử dụng Long để đại diện cho timestamp
        releaseYear = new Date(in.readLong());
        coverImage = in.readString();
        genreId = in.readInt();
    }

    public static final Creator<Album> CREATOR = new Creator<Album>() {
        @Override
        public Album createFromParcel(Parcel in) {
            return new Album(in);
        }

        @Override
        public Album[] newArray(int size) {
            return new Album[size];
        }
    };

    public String getAlbumId() {
        return albumId;
    }

    public void setAlbumId(String albumId) {
        this.albumId = albumId;
    }

    public int getArtistId() {
        return artistId;
    }

    public void setArtistId(int artistId) {
        this.artistId = artistId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(Date releaseYear) {
        this.releaseYear = releaseYear;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    public int getGenreId() {
        return genreId;
    }

    public void setGenreId(int genreId) {
        this.genreId = genreId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int i) {
        dest.writeString(albumId);
        dest.writeInt(artistId);
        dest.writeString(title);
        // Đối với kiểu Date, bạn có thể sử dụng getTime() để lấy timestamp
        dest.writeLong(releaseYear.getTime());
        dest.writeString(coverImage);
        dest.writeInt(genreId);
    }
}