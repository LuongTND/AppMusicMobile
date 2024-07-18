package com.example.echobeat.fragment;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.echobeat.R;
import com.example.echobeat.apdater.PlaylistAdapter;
import com.example.echobeat.dbFirebase.FirebaseHelper;
import com.example.echobeat.modelFirebase.Playlist;


import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    private RecyclerView recyclerView;
    private PlaylistAdapter playlistAdapter;
    private List<Playlist> playlistList;
    private List<Playlist> userPlaylist;
    private int userId = 2;

//    SessionManager sessionManager = new SessionManager(this);
//    String googleId = sessionManager.getGoogleId();
//    String userId = sessionManager.getUserid();
    private static final String TAG = "ProfileFragment";

    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        playlistList = new ArrayList<>();
        userPlaylist = new ArrayList<>();

        playlistAdapter = new PlaylistAdapter(getContext(), userPlaylist);
        recyclerView.setAdapter(playlistAdapter);

        loadData();

        return view;
    }

    private void loadData() {
        FirebaseHelper<Playlist> playlistFirebaseHelper = new FirebaseHelper<>();
        playlistFirebaseHelper.getData("playlists", Playlist.class, new FirebaseHelper.DataCallback<Playlist>() {
            @Override
            public void onCallback(List<Playlist> data) {
                if (data != null) {
                    playlistList.clear();
                    playlistList.addAll(data);
                    Log.d(TAG, "Loaded playlists: " + playlistList.size());

                    userPlaylist.clear();
                    for (Playlist p : playlistList) {
                        if (p.getUserId() == userId) {
                            userPlaylist.add(p);
                        }
                    }

                    Log.d(TAG, "Filtered user playlists: " + userPlaylist.size());
                    playlistAdapter.notifyDataSetChanged();

                    // Log the contents of playlistList and userPlaylist
                    for (Playlist p : playlistList) {
                        Log.d(TAG, "Playlist: " + p.getName() + ", UserId: " + p.getUserId());
                    }
                    for (Playlist p : userPlaylist) {
                        Log.d(TAG, "User Playlist: " + p.getName() + ", UserId: " + p.getUserId());
                    }

                } else {
                    Log.e(TAG, "Failed to load playlists.");
                }
            }
        });
    }
}