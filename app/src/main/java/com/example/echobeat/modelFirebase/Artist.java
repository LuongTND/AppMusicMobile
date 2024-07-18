package com.example.echobeat.modelFirebase;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.List;

public class Artist extends User implements Parcelable {
    private String artistId;

    private String artistName;
    private String bio;
    private List<String> songIds;
    private String musicGenre;



    // Constructors, getters, and setters
    public Artist() {
        super();
    }

    public Artist(String userId, String username, String email, String profilePicture, int roleId, String googleId, String artistId,String artistName, String bio, List<String> songIds, String musicGenre) {
        super(userId, username, email, profilePicture, roleId, googleId);
        this.artistId = artistId;
        this.artistName = artistName;
        this.bio = bio;
        this.songIds = songIds;
        this.musicGenre = musicGenre;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }
    protected Artist(Parcel in) {
        artistId = in.readString();
        bio = in.readString();
        songIds = in.createStringArrayList();
        musicGenre = in.readString();
    }

    public static final Creator<Artist> CREATOR = new Creator<Artist>() {
        @Override
        public Artist createFromParcel(Parcel in) {
            return new Artist(in);
        }

        @Override
        public Artist[] newArray(int size) {
            return new Artist[size];
        }
    };

    public String getArtistId() {
        return artistId;
    }

    public void setArtistId(String artistId) {
        this.artistId = artistId;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public List<String> getSongIds() {
        return songIds;
    }

    public void setSongIds(List<String> songIds) {
        this.songIds = songIds;
    }

    public String getMusicGenre() {
        return musicGenre;
    }

    public void setMusicGenre(String musicGenre) {
        this.musicGenre = musicGenre;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(artistId);
        parcel.writeString(bio);
        parcel.writeStringList(songIds);
        parcel.writeString(musicGenre);
    }
}
