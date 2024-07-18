package com.example.echobeat.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.echobeat.R;
import com.example.echobeat.activity.AlbumDetailActivity;
import com.example.echobeat.activity.ArtisSongActivity;
import com.example.echobeat.activity.PlayerActivity;
import com.example.echobeat.apdater.AlbumAdapter;
import com.example.echobeat.apdater.ArtistAdapter;
import com.example.echobeat.apdater.HistoryApdater;
import com.example.echobeat.apdater.SongAdapter;
import com.example.echobeat.dbFirebase.FirebaseHelper;
import com.example.echobeat.modelFirebase.Album;
import com.example.echobeat.modelFirebase.Artist;
import com.example.echobeat.modelFirebase.History;
import com.example.echobeat.modelFirebase.Song;
import com.example.echobeat.viewModel.AllFragmentViewModel;

import java.util.ArrayList;
import java.util.List;

public class AllFragment extends Fragment {

    private RecyclerView recyclerViewSongs;
    private RecyclerView recyclerViewAlbums;
    private RecyclerView recyclerViewArtists;
    private RecyclerView recyclerViewHistory;

    private SongAdapter songAdapter;
    private AlbumAdapter albumAdapter;
    private ArtistAdapter artistAdapter;
    private HistoryApdater historyApdater;
    private AllFragmentViewModel viewModel;




    public AllFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);  // Retain this fragment across configuration changes

    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_all, container, false);

        // Initialize RecyclerView
        recyclerViewSongs = rootView.findViewById(R.id.recycler_view_songs);
        recyclerViewSongs.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        recyclerViewArtists = rootView.findViewById(R.id.recycler_view_artists);
        recyclerViewArtists.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        recyclerViewAlbums = rootView.findViewById(R.id.recycler_view_albums);
        recyclerViewAlbums.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        recyclerViewHistory = rootView.findViewById(R.id.recycler_view_history);
        recyclerViewHistory.setLayoutManager(new GridLayoutManager(getContext(), 2)); // 2 columns grid layout

        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(AllFragmentViewModel.class);

        viewModel.getHistory().observe(getViewLifecycleOwner(), new Observer<List<History>>() {
            @Override
            public void onChanged(List<History> histories) {
                if (histories != null) {
                    historyApdater = new HistoryApdater(getContext(), histories);
                    historyApdater.setOnItemClickListener(new HistoryApdater.OnItemClickListener() {
                        @Override
                        public void onItemClick(History history) {
                            switch (history.getType()) {
                                case "song":
                                    // Chuyển sang Activity hiển thị thông tin của bài hát
                                    Intent songIntent = new Intent(getContext(), PlayerActivity.class);
                                    songIntent.putExtra("SONG_ID", history.getItemId());
                                    startActivity(songIntent);
                                    break;
                                case "album":
                                    // Chuyển sang Activity hiển thị thông tin của album
                                    Intent albumIntent = new Intent(getContext(), AlbumDetailActivity.class);
                                    albumIntent.putExtra("ALBUM_ID", history.getItemId());
                                    startActivity(albumIntent);
                                    break;
                                default:
                                    // Xử lý khi không có type phù hợp
                                    break;
                            }
                        }
                    });
                    recyclerViewHistory.setAdapter(historyApdater);
                }
            }
        });

        // Observe the ViewModel data
        viewModel.getSongs().observe(getViewLifecycleOwner(), new Observer<List<Song>>() {
            @Override
            public void onChanged(List<Song> songs) {
                if (songs != null) {
                    songAdapter = new SongAdapter(getContext(), songs);
                    songAdapter.setOnItemClickListener(new SongAdapter.OnItemClickListener() {
                        // Inside onItemClick method in SongAdapter.OnItemClickListener
                        @Override
                        public void onItemClick(Song song) {
                            if (getContext() != null) {
                                // Ensure context is valid before showing Toast
                                Toast.makeText(getContext(), "Play song: " + song.getTitle(), Toast.LENGTH_SHORT).show();
                                // Pass song list and selected song to PlayerActivity
                                Intent intent = new Intent(getContext(), PlayerActivity.class);
                                intent.putParcelableArrayListExtra("SONG_LIST", (ArrayList<Song>) songs);
                                intent.putExtra("SONG_DATA", song); // Pass selected song
                                intent.putExtra("SONG_URL", song.getSongUrl());
                                startActivity(intent);
                            }
                        }

                    });
                    recyclerViewSongs.setAdapter(songAdapter);
                }
            }
        });

        viewModel.getArtists().observe(getViewLifecycleOwner(), new Observer<List<Artist>>() {
            @Override
            public void onChanged(List<Artist> artists) {
                if (artists != null) {
                    artistAdapter = new ArtistAdapter(getContext(), artists);
                    artistAdapter.setOnItemClickListener(new ArtistAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(Artist artist) {
                            if (getContext() != null) {
                                // Ensure context is valid before showing Toast
                                Toast.makeText(getContext(), "You had choose: " + artist.getUsername(), Toast.LENGTH_SHORT).show();
                                // Pass song list and selected song to PlayerActivity
                                Intent intent = new Intent(getContext(), ArtisSongActivity.class);
                                intent.putParcelableArrayListExtra("SONG_LIST", (ArrayList<Artist>) artists);
                                startActivity(intent);
                            }
                        }
                    });
                    recyclerViewArtists.setAdapter(artistAdapter);
                }
            }
        });

        viewModel.getAlbums().observe(getViewLifecycleOwner(), new Observer<List<Album>>() {
            @Override
            public void onChanged(List<Album> albums) {
                if (albums != null) {
                    albumAdapter = new AlbumAdapter(getContext(), albums);
                    albumAdapter.setOnItemClickListener(new AlbumAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(Album album) {
                            if (getContext() != null) {
                                // Ensure context is valid before showing Toast
                                Toast.makeText(getContext(), "You had choose: " + album.getTitle(), Toast.LENGTH_SHORT).show();
                                // Pass song list and selected song to PlayerActivity
                                Intent intent = new Intent(getContext(), AlbumDetailActivity.class);
                                intent.putParcelableArrayListExtra("SONG_LIST", (ArrayList<Album>) albums);
                                startActivity(intent);
                            }
                        }
                    });
                    recyclerViewAlbums.setAdapter(albumAdapter);
                }
            }
        });

        // Load data if ViewModel is empty
        if (!viewModel.isSongsLoaded()) {
            loadSongs();
        }

        if (!viewModel.isArtistsLoaded()) {
            loadArtists();
        }

        if (!viewModel.isAlbumsLoaded()) {
            loadAlbums();
        }

        if(!viewModel.isHistoryLoaded()){
            loadHistory();
        }

        return rootView;
    }

    private void loadHistory() {
        FirebaseHelper<History> firebaseHelper = new FirebaseHelper<>();
        firebaseHelper.getRecentData("histories", "timestamp", 4, History.class, new FirebaseHelper.DataCallback<History>() {
            @Override
            public void onCallback(List<History> data) {
                if (data != null) {
                    viewModel.setHistory(data);
                }
            }
        });
    }

    private void loadArtists() {
        FirebaseHelper<Artist> firebaseHelper = new FirebaseHelper<>();
        firebaseHelper.getRecentData("artists", "username", 20, Artist.class, new FirebaseHelper.DataCallback<Artist>() {
            @Override
            public void onCallback(List<Artist> data) {
                if (data != null) {
                    viewModel.setArtists(data);
                }
            }
        });
    }

    private void loadAlbums() {
        FirebaseHelper<Album> firebaseHelper = new FirebaseHelper<>();
        firebaseHelper.getRecentData("albums", "releaseYear", 20, Album.class, new FirebaseHelper.DataCallback<Album>() {
            @Override
            public void onCallback(List<Album> data) {
                if (data != null) {
                    viewModel.setAlbums(data);
                }
            }
        });
    }

    private void loadSongs() {
        FirebaseHelper<Song> firebaseHelper = new FirebaseHelper<>();
        firebaseHelper.getRecentData("songs", "releaseYear", 20, Song.class, new FirebaseHelper.DataCallback<Song>() {
            @Override
            public void onCallback(List<Song> data) {
                if (data != null) {
                    viewModel.setSongs(data);
                }
            }
        });
    }



}
