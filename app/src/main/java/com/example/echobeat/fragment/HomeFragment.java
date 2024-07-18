package com.example.echobeat.fragment;

import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.echobeat.R;

public class HomeFragment extends Fragment {



    private Toolbar toolbar;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);


        // Initialize toolbar
        toolbar = rootView.findViewById(R.id.toolbar);
        setupToolbar();

        // Load default fragment
        loadFragment(new AllFragment());



        return rootView;
    }

    private void setupToolbar() {
        toolbar.inflateMenu(R.menu.home_toolbar_menu);
        toolbar.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.action_all) {
                loadFragment(new AllFragment());
                return true;
            } else if (itemId == R.id.action_songs) {
                loadFragment(new SongFragment());
                return true;
            }
            return false;
        });
    }

    private void loadFragment(Fragment fragment) {
        if (fragment != null) {
            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, fragment);
            transaction.commit();
        }
    }


}
