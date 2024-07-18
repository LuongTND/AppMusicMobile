package com.example.echobeat;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.echobeat.activity.LoginActivity;
import com.example.echobeat.activity.SettingsActivity;
import com.example.echobeat.activity.loginModel.OptionRole;
import com.example.echobeat.dbFirebase.SeedData;
import com.example.echobeat.fragment.HomeFragment;
import com.example.echobeat.fragment.LibraryFragment;
import com.example.echobeat.fragment.ProfileFragment;
import com.example.echobeat.fragment.SearchFragment;
import com.example.echobeat.session.SessionManager;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;


public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private NavigationView navigationView;
    private BottomNavigationView bottomNavigationView;
    private FirebaseAuth auth;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
         //Khong seed data nua
//        SeedData seedData = new SeedData();
//        seedData.seedAllData();

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Setup the ActionBarDrawerToggle to enable swiping the drawer
        setupDrawer();

        // Load the default fragment
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.nav_host_fragment, new HomeFragment())
                    .commit();
        }

        // Handle navigation item clicks in the bottom navigation
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.navigation_home) {
                selectedFragment = new HomeFragment();
            } else if (itemId == R.id.navigation_search) {
                selectedFragment = new SearchFragment();
            } else if (itemId == R.id.navigation_library) {
                SessionManager sessionManager = new SessionManager(this);
                if (sessionManager.getGoogleId() == null) {
                    // Nếu Google ID k tồn tại trong session, chuyển sang Login
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                }else {
                    selectedFragment = new LibraryFragment();
                }
//                selectedFragment = new LibraryFragment();
            }

            // Replace the current fragment with the selected one
            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.nav_host_fragment, selectedFragment)
                        .commit();
                return true;
            }
            return false;
        });

        // Handle navigation item clicks in the drawer
        navigationView.setNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.menu_profile) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, new ProfileFragment());
                transaction.addToBackStack(null);
                transaction.commit();
            }else if (itemId == R.id.menu_settings) {
                // Handle settings click
             startActivity(new Intent(MainActivity.this, SettingsActivity.class));
            } else if (itemId == R.id.menu_login) {
                // Handle login click
                SessionManager sessionManager = new SessionManager(this);
                if (sessionManager.getGoogleId() == null) {
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                }else {
                    Toast.makeText(MainActivity.this, "Ban da login", Toast.LENGTH_SHORT).show();
                }

            } else if (itemId == R.id.menu_logout) {
                // Handle logout click
                SessionManager sessionManager = new SessionManager(this);
                if (sessionManager.getGoogleId() != null) {
                    signOut();
                }else {
                    Toast.makeText(MainActivity.this, "Ban chua Login", Toast.LENGTH_SHORT).show();
                }
            }
            // Close the drawer
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
    }

    private void setupDrawer() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerToggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void signOut() {
        auth.signOut();
        mGoogleSignInClient.signOut().addOnCompleteListener(this, task -> {
            Toast.makeText(MainActivity.this, "Signed Out", Toast.LENGTH_SHORT).show();

            //đặt lại session
            SessionManager sessionManager = new SessionManager(this);
            sessionManager.clearSession();

            // Chuyển về màn hình đăng nhập
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}