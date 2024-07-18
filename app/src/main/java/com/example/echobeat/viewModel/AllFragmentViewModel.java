package com.example.echobeat.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.echobeat.modelFirebase.Album;
import com.example.echobeat.modelFirebase.Artist;
import com.example.echobeat.modelFirebase.History;
import com.example.echobeat.modelFirebase.Song;

import java.util.List;

public class AllFragmentViewModel extends ViewModel {
    private final MutableLiveData<List<Song>> songs = new MutableLiveData<>();
    private final MutableLiveData<List<Album>> albums = new MutableLiveData<>();
    private final MutableLiveData<List<Artist>> artists = new MutableLiveData<>();
    private final MutableLiveData<List<History>> historyList = new MutableLiveData<>();

    public LiveData<List<Song>> getSongs() {
        return songs;
    }

    public LiveData<List<History>> getHistory() {
        return historyList;
    }
    public boolean isHistoryLoaded() {
        return historyList.getValue() != null;
    }

    public void setHistory(List<History> history) {
        historyList.setValue(history);
    }

    public void setSongs(List<Song> songList) {
        songs.setValue(songList);
    }

    public boolean isSongsLoaded() {
        return songs.getValue() != null;
    }

    public LiveData<List<Album>> getAlbums() {
        return albums;
    }

    public void setAlbums(List<Album> albumList) {
        albums.setValue(albumList);
    }

    public boolean isAlbumsLoaded() {
        return albums.getValue() != null;
    }

    public LiveData<List<Artist>> getArtists() {
        return artists;
    }

    public void setArtists(List<Artist> artistList) {
        artists.setValue(artistList);
    }

    public boolean isArtistsLoaded() {
        return artists.getValue() != null;
    }
}
