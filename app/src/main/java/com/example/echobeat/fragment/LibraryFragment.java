package com.example.echobeat.fragment;

import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.echobeat.R;
import com.example.echobeat.apdater.SongAdapter;
import com.example.echobeat.modelFirebase.Song;
import com.example.echobeat.repository.SongRepository;

import java.util.List;


public class LibraryFragment extends Fragment {

    private RecyclerView recyclerViewLibrary;

    private SongAdapter songAdapter;
    private SongRepository songRepository;
    public LibraryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_library, container, false);

        // Load default fragment
        loadFragment(new PlaylistFragment());

//        View rootView = inflater.inflate(R.layout.fragment_library, container, false);
//        recyclerViewLibrary = rootView.findViewById(R.id.recyclerViewLibrary);
//        recyclerViewLibrary.setLayoutManager(new LinearLayoutManager(getContext()));
//        songRepository = new SongRepository(getContext());
//        List<Song> songList = songRepository.getAllSongsFromPlaylist();
//        songAdapter = new SongAdapter(getContext(), songList);
//        recyclerViewLibrary.setAdapter(songAdapter);

        return rootView;
    }
    private void loadFragment(Fragment fragment) {
        if (fragment != null) {
            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, fragment);
            transaction.commit();
        }
    }
}