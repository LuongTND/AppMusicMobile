package com.example.echobeat.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.echobeat.R;
import com.example.echobeat.activity.PlayerActivity;
import com.example.echobeat.apdater.ListPlaylistAdapter;
import com.example.echobeat.apdater.SongAdapter;
import com.example.echobeat.dbFirebase.FirebaseHelper;
import com.example.echobeat.modelFirebase.Song;
import com.example.echobeat.repository.SongRepository;
import com.example.echobeat.viewModel.AllFragmentViewModel;
import com.example.echobeat.viewModel.PlaylistFragmentViewModel;

import java.util.ArrayList;
import java.util.List;


public class PlaylistFragment extends Fragment {

    private RecyclerView recyclerViewPlayLists;
    private ListPlaylistAdapter ListPlaylistAdapter;
    private PlaylistFragmentViewModel viewModel;

    private SongAdapter songAdapter;
    private SongRepository songRepository;
    public PlaylistFragment(){

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
        View rootView = inflater.inflate(R.layout.fragment_playlist, container, false);
        // Initialize RecyclerView
        recyclerViewPlayLists = rootView.findViewById(R.id.recycler_view_playLists);
        recyclerViewPlayLists.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(PlaylistFragmentViewModel.class);
        // Observe the ViewModel data
        viewModel.getSongs().observe(getViewLifecycleOwner(), new Observer<List<Song>>() {
            @Override
            public void onChanged(List<Song> songs) {
                if (songs != null) {
                    ListPlaylistAdapter = new ListPlaylistAdapter(getContext(), songs);
                    ListPlaylistAdapter.setOnItemClickListener(new ListPlaylistAdapter.OnItemClickListener() {

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
                    recyclerViewPlayLists.setAdapter(ListPlaylistAdapter);
                }
            }
        });
        // Load data if ViewModel is empty
        if (!viewModel.isSongsLoaded()) {
            loadSongs();
        }
        return rootView;
    }
//public PlaylistFragment() {
//    // Required empty public constructor
//}
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setRetainInstance(true);  // Retain this fragment across configuration changes
//    }
//
//    @SuppressLint("MissingInflatedId")
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        View rootView = inflater.inflate(R.layout.fragment_playlist, container, false);
//        // Initialize RecyclerView
//        recyclerViewPlayLists = rootView.findViewById(R.id.recycler_view_playLists);
//        recyclerViewPlayLists.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
//
//        // Initialize ViewModel
//        viewModel = new ViewModelProvider(this).get(PlaylistFragmentViewModel.class);
//
//        // Initialize Repository
//        songRepository = new SongRepository(getContext());
//
//        // Observe the ViewModel data
//        viewModel.getSongs().observe(getViewLifecycleOwner(), new Observer<List<Song>>() {
//            @Override
//            public void onChanged(List<Song> songs) {
//                if (songs != null) {
//                    songAdapter = new SongAdapter(getContext(), songs);
//                    recyclerViewPlayLists.setAdapter(songAdapter);
//                }
//            }
//        });
//
//        // Load data from SQLite if ViewModel is empty
//        if (!viewModel.isSongsLoaded()) {
//            loadSongsFromDatabase();
//        }
//
//        return rootView;
//    }
//
//    private void loadSongsFromDatabase() {
//        List<Song> songs = songRepository.getAllSongsFromPlaylist();
//        if (songs != null) {
//            viewModel.setSongs(songs);
//        }
//    }
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