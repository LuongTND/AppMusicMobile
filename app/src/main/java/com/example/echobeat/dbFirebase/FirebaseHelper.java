package com.example.echobeat.dbFirebase;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.echobeat.modelFirebase.Album;
import com.example.echobeat.modelFirebase.Playlist;
import com.example.echobeat.modelFirebase.ResultSearch;
import com.example.echobeat.modelFirebase.Song;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

public class FirebaseHelper<T> {
    private static final String TAG = "FirebaseHelper";
    private FirebaseFirestore db;

    public FirebaseHelper() {
        this.db = FirebaseFirestore.getInstance();
    }

    public void addData(String collection, T data) {
        db.collection(collection)
                .add(data)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
    }

    public void getData(String collection, final Class<T> clazz, final DataCallback<T> callback) {
        db.collection(collection)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<T> dataList = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                T item = document.toObject(clazz);
                                dataList.add(item);
                            }
                            callback.onCallback(dataList);
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                            callback.onCallback(null);
                        }
                    }
                });
    }

    private void checkCollectionExists(String collection, CollectionExistsCallback callback) {
        db.collection(collection)
                .limit(1)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            boolean exists = !task.getResult().isEmpty();
                            callback.onCallback(exists);
                        } else {
                            Log.w(TAG, "Error checking if collection exists.", task.getException());
                            callback.onCallback(false);
                        }
                    }
                });
    }

    public void search(String query, SearchCallback<ResultSearch> callback) {
        // Implement Firebase search logic here
        // Once search is complete, call the callback with the results
        List<ResultSearch> searchResults = new ArrayList<>();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("songs")
                .whereEqualTo("title", query)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            searchResults.add(document.toObject(ResultSearch.class));
                        }
                        callback.onSearchComplete(searchResults);
                    } else {
                        // Handle the error
                    }
                });
    }

    public void searchSongs(String query, Consumer<List<Song>> callback) {
        db.collection("songs")
                .whereGreaterThanOrEqualTo("title", query)
                .whereLessThanOrEqualTo("title", query + "\uf8ff")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Song> songs = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Song song = document.toObject(Song.class);
                            songs.add(song);
                        }
                        callback.accept(songs);
                    } else {
                        callback.accept(new ArrayList<>());
                    }
                });
    }

    public void getAllData(String collection, final Class<T> clazz, final DataCallback<T> callback) {
        db.collection(collection)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<T> dataList = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                T item = document.toObject(clazz);
                                dataList.add(item);
                            }
                            callback.onCallback(dataList);
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                            callback.onCallback(null);
                        }
                    }
                });
    }

    public void getRecentData(String collection, String orderByField, int limit, final Class<T> clazz, final DataCallback<T> callback) {
        db.collection(collection)
                .orderBy(orderByField, Query.Direction.DESCENDING)
                .limit(limit)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<T> dataList = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                T item = document.toObject(clazz);
                                dataList.add(item);
                            }
                            callback.onCallback(dataList);
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                            callback.onCallback(null);
                        }
                    }
                });
    }


    public void getRandomSong(final SongCallback callback) {
        db.collection("songs")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<Song> songs = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Song song = document.toObject(Song.class);
                                songs.add(song);
                            }
                            if (!songs.isEmpty()) {
                                Random random = new Random();
                                int randomIndex = random.nextInt(songs.size());
                                Song randomSong = songs.get(randomIndex);
                                callback.onSongLoaded(randomSong);
                            } else {
                                callback.onSongLoaded(null); // Handle case when no songs are available
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                            callback.onSongLoaded(null);
                        }
                    }
                });
    }

    public void getRandomAlbum(final AlbumCallback callback) {
        db.collection("albums")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<Album> albums = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Album album = document.toObject(Album.class);
                                albums.add(album);
                            }
                            if (!albums.isEmpty()) {
                                Random random = new Random();
                                int randomIndex = random.nextInt(albums.size());
                                Album randomAlbum = albums.get(randomIndex);
                                callback.onAlbumLoaded(randomAlbum);
                            } else {
                                callback.onAlbumLoaded(null); // Handle case when no albums are available
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                            callback.onAlbumLoaded(null);
                        }
                    }
                });
    }

    public void getRandomPlaylist(final PlaylistCallback callback) {
        db.collection("playlists")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<Playlist> playlists = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Playlist playlist = document.toObject(Playlist.class);
                                playlists.add(playlist);
                            }
                            if (!playlists.isEmpty()) {
                                Random random = new Random();
                                int randomIndex = random.nextInt(playlists.size());
                                Playlist randomPlaylist = playlists.get(randomIndex);
                                callback.onPlaylistLoaded(randomPlaylist);
                            } else {
                                callback.onPlaylistLoaded(null); // Handle case when no playlists are available
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                            callback.onPlaylistLoaded(null);
                        }
                    }
                });
    }
    public void getRandomSongs(final int count, final DataCallback<Song> callback) {
        db.collection("songs")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<Song> songs = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Song song = document.toObject(Song.class);
                                songs.add(song);
                            }
                            if (songs.size() > 0) {
                                Random random = new Random();
                                List<Song> randomSongs = new ArrayList<>();
                                for (int i = 0; i < count; i++) {
                                    int randomIndex = random.nextInt(songs.size());
                                    randomSongs.add(songs.get(randomIndex));
                                    songs.remove(randomIndex);
                                }
                                callback.onCallback(randomSongs);
                            } else {
                                callback.onCallback(null); // Handle case when no songs are available
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                            callback.onCallback(null);
                        }
                    }
                });
    }


    public void UpdateArtist(){}


    private interface CollectionExistsCallback {
        void onCallback(boolean exists);
    }

    public interface DataCallback<T> {
        void onCallback(List<T> data);
    }


    public interface SongCallback {
        void onSongLoaded(Song song);
    }

    public interface AlbumCallback {
        void onAlbumLoaded(Album album);
    }

    public interface PlaylistCallback {
        void onPlaylistLoaded(Playlist playlist);
    }

    // Interface callback để trả về kết quả tìm kiếm
    public interface SearchCallback<R> {
        void onSearchComplete(List<ResultSearch> searchResults);
    }
}