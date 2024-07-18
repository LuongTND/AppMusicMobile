package com.example.echobeat.fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.echobeat.R;
import com.example.echobeat.apdater.SongAdapter;
import com.example.echobeat.dbFirebase.FirebaseHelper;
import com.example.echobeat.modelFirebase.History;
import com.example.echobeat.modelFirebase.Song;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SongFragment extends Fragment {

    private RecyclerView recyclerViewSongs;
    private RecyclerView recyclerViewRankings;
    private RecyclerView recyclerViewRelatedSongs;

    private List<Song> newSongsList = new ArrayList<>();
    private List<Song> rankingSongsList = new ArrayList<>();
    private List<Song> relatedSongsList = new ArrayList<>();

    private SongAdapter newSongsAdapter;
    private SongAdapter rankingSongsAdapter;
    private SongAdapter relatedSongsAdapter;

    private List<Song> songList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_song, container, false);

        // Initialize RecyclerView and set its adapter
        recyclerViewSongs = view.findViewById(R.id.recycler_view_songs);
        recyclerViewRankings = view.findViewById(R.id.recycler_view_rankings);
        recyclerViewRelatedSongs = view.findViewById(R.id.recycler_view_related_songs);

        recyclerViewSongs.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerViewRankings.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerViewRelatedSongs.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        // Initialize adapters with their respective lists
        newSongsAdapter = new SongAdapter(getContext(), newSongsList);
        rankingSongsAdapter = new SongAdapter(getContext(), rankingSongsList);
        relatedSongsAdapter = new SongAdapter(getContext(), relatedSongsList);

        // Set adapters for RecyclerViews
        recyclerViewSongs.setAdapter(newSongsAdapter);
        recyclerViewRankings.setAdapter(rankingSongsAdapter);
        recyclerViewRelatedSongs.setAdapter(relatedSongsAdapter);

        // Load data for each RecyclerView
        loadNewSongsFromFirestore();
        loadRankingSongsFromFirestore();
        loadRelatedSongsFromFirestore();

        return view;
    }

    private void loadNewSongsFromFirestore() {
        FirebaseHelper<Song> firebaseHelper = new FirebaseHelper<>();
        firebaseHelper.getRecentData("songs", "releaseYear", 10, Song.class, new FirebaseHelper.DataCallback<Song>() {
            @Override
            public void onCallback(List<Song> data) {
                if (data != null) {
                    newSongsList.clear();
                    newSongsList.addAll(data);
                    newSongsAdapter.notifyDataSetChanged();
                } else {
                    Log.e("SongFragment", "Failed to load new songs from Firestore");
                }
            }
        });
    }

    private void loadRankingSongsFromFirestore() {
        FirebaseHelper<Song> firebaseHelper = new FirebaseHelper<>();
        firebaseHelper.getRecentData("songs", "playCount", 10, Song.class, new FirebaseHelper.DataCallback<Song>() {
            @Override
            public void onCallback(List<Song> data) {
                if (data != null) {
                    rankingSongsList.clear();
                    rankingSongsList.addAll(data);
                    rankingSongsAdapter.notifyDataSetChanged();
                } else {
                    Log.e("SongFragment", "Failed to load ranking songs from Firestore");
                }
            }
        });
    }

    private void loadRelatedSongsFromFirestore() {
        FirebaseHelper<History> firebaseHelper = new FirebaseHelper<>();
        firebaseHelper.getRecentData("histories", "timestamp", 4, History.class, new FirebaseHelper.DataCallback<History>() {
            @Override
            public void onCallback(List<History> historyData) {
                if (historyData != null) {
                    // Set để lưu trữ các categoryId duy nhất
                    Set<String> uniqueCategoryIds = new HashSet<>();
                    String categoryId = "";
                    // Lặp qua historyData để lấy ra các categoryId và lưu vào Set
                    for (int i = 0; i< 4; i++) {
                        if(!historyData.get(i).getType().equals("song")){
                            String itemId = historyData.get(i).getItemId();
                            // Lấy categoryId từ itemId và thêm vào Set
                            for (int j = 0; j < songList.size(); j++) {
                                if (songList.get(j).getSongId().equals(itemId)) {
                                    categoryId = songList.get(j).getCategoryId();
                                    Log.d("SongFragment", categoryId);
                                    uniqueCategoryIds.add(categoryId);
                                    break;
                                }
                            }
                        } else {
                            categoryId = historyData.get(i).getItemId();
                        }
                        if (categoryId != null) {
                            uniqueCategoryIds.add(categoryId);
                        }
                    }
                    Log.d("SongFragment", "Unique categoryIds: " + uniqueCategoryIds.toString());
                    // Tiếp tục xử lý với uniqueCategoryIds, ví dụ như tải danh sách bài hát cho từng categoryId từ Firestore
                    FirebaseHelper<Song> firebaseHelper = new FirebaseHelper<>();
                    for (String categoryIds : uniqueCategoryIds) {
                        firebaseHelper.getRecentData("songs", "playCount", 5, Song.class, new FirebaseHelper.DataCallback<Song>() {
                            @Override
                            public void onCallback(List<Song> data) {
                                for (Song song : data) {
                                    relatedSongsList.add(song);
                                    relatedSongsAdapter.notifyDataSetChanged();
                                }
                            }
                        });
                    }

                    // Ví dụ: loadSongsByCategoryIds(uniqueCategoryIds);
                } else {
                    Log.e("SongFragment", "Failed to load history data from Firestore");
                }
            }
        });
    }




}