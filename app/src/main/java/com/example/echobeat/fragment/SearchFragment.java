package com.example.echobeat.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.echobeat.R;
import com.example.echobeat.apdater.SearchResultsAdapter;
import com.example.echobeat.dbFirebase.FirebaseHelper;
import com.example.echobeat.modelFirebase.Album;
import com.example.echobeat.modelFirebase.Artist;
import com.example.echobeat.modelFirebase.ResultSearch;
import com.example.echobeat.modelFirebase.Song;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {

    private View rootView;
    private SearchView searchView;
    private RecyclerView recyclerView;
    private TextView textContent;

    private List<ResultSearch> resultList;
    private SearchResultsAdapter adapter;

    private FirebaseHelper<Song> songHelper;
    private FirebaseHelper<Album> albumHelper;
    private FirebaseHelper<Artist> artistHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_search, container, false);

        searchView = rootView.findViewById(R.id.search_view);
        recyclerView = rootView.findViewById(R.id.recycler_search_results);
        textContent = rootView.findViewById(R.id.text_content);

        resultList = new ArrayList<>();
        adapter = new SearchResultsAdapter(resultList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

        songHelper = new FirebaseHelper<>();
        albumHelper = new FirebaseHelper<>();
        artistHelper = new FirebaseHelper<>();

        loadAllData();

        setupSearchView();

        RelativeLayout relativeLayout = rootView.findViewById(R.id.relative_layout);
        relativeLayout.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (searchView != null && !searchView.isIconified()) {
                        searchView.setQuery("", false);
                        searchView.clearFocus();
                        searchView.setIconified(true);
                    }
                }
                return false;
            }
        });

        return rootView;
    }

    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterResults(newText);
                if (newText.isEmpty()) {
                    showInitialContent();
                } else {
                    showSearchResults();
                }
                return true;
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                hideSearchResults();
                return false;
            }
        });

        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideSearchResults();
                }
            }
        });
    }

    private void loadAllData() {
        songHelper.getAllData("songs", Song.class, this::addResultsToList);
        albumHelper.getAllData("albums", Album.class, this::addResultsToList);
        artistHelper.getAllData("artists", Artist.class, this::addResultsToList);
    }

    private <T> void addResultsToList(List<T> dataList) {
        if (dataList != null) {
            for (T data : dataList) {
                if (data instanceof Song) {
                    resultList.add(new ResultSearch(((Song) data).getTitle(), "Song"));
                } else if (data instanceof Album) {
                    resultList.add(new ResultSearch(((Album) data).getTitle(), "Album"));
                } else if (data instanceof Artist) {
                    resultList.add(new ResultSearch(((Artist) data).getUsername(), "Artist"));
                }
            }
            adapter.notifyDataSetChanged();
        }
    }

    private void filterResults(String newText) {
        List<ResultSearch> filteredList = new ArrayList<>();
        for (ResultSearch result : resultList) {
            if (result.getTitle().toLowerCase().contains(newText.toLowerCase())) {
                filteredList.add(result);
            }
        }
        adapter.updateData(filteredList);
    }

    private void showSearchResults() {
        recyclerView.setVisibility(View.VISIBLE);
        textContent.setVisibility(View.GONE);
    }

    private void hideSearchResults() {
        recyclerView.setVisibility(View.GONE);
        textContent.setVisibility(View.VISIBLE);
    }

    private void showInitialContent() {
        recyclerView.setVisibility(View.GONE);
        textContent.setVisibility(View.VISIBLE);
    }
}
