package com.example.echobeat.activity;

import static com.example.echobeat.dbFirebase.SeedData.getCurrentDateTime;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.echobeat.R;
import com.example.echobeat.dbFirebase.FirebaseHelper;
import com.example.echobeat.modelFirebase.Album;
import com.example.echobeat.modelFirebase.Song;
import com.example.echobeat.repository.UserRepository;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class UploadAlbum extends AppCompatActivity {

    private static final int PICK_SONG_REQUEST = 1;
    private static final int PICK_IMAGE_REQUEST = 2;
    private Button btnSelectSong, btnUploadAlbum, btnUploadImage;
    private EditText etAlbumTitle, etSongTitle; // Added etSongTitle for song title
    private ProgressBar progressBar;
    private ImageView ivAlbumImage;
    private Uri imageUri;
    private FirebaseHelper<Song> songFirebaseHelper;
    private FirebaseHelper<Album> albumFirebaseHelper;
    private List<Song> songs = new ArrayList<>();
    private RecyclerView rvSelectedSongs;
    private SongsAdapter songsAdapter;
    private UserRepository userRepository;

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private NavigationView navigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_album);
        etAlbumTitle = findViewById(R.id.etAlbumTitle);
        etSongTitle = findViewById(R.id.etSongTitle); // Initialize etSongTitle
        btnSelectSong = findViewById(R.id.btnSelectSong);
        btnUploadAlbum = findViewById(R.id.btnUploadAlbum);
        btnUploadImage = findViewById(R.id.btnUploadImage);
        rvSelectedSongs = findViewById(R.id.rvSelectedSongs);
        progressBar = findViewById(R.id.progressBar);
        ivAlbumImage = findViewById(R.id.ivAlbumImage);

        songFirebaseHelper = new FirebaseHelper<>();
        albumFirebaseHelper = new FirebaseHelper<>();
        userRepository = new UserRepository(getApplicationContext());

        songsAdapter = new SongsAdapter(songs);
        rvSelectedSongs.setLayoutManager(new LinearLayoutManager(this));
        rvSelectedSongs.setAdapter(songsAdapter);

        drawerLayout = findViewById(R.id.drawer_artist_uploadAlbum);
        navigationView = findViewById(R.id.nav_view_artist_album);
        setupDrawer();

        btnSelectSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser(PICK_SONG_REQUEST);
            }
        });

        btnUploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser(PICK_IMAGE_REQUEST);
            }
        });

        btnUploadAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageUri != null && !etAlbumTitle.getText().toString().isEmpty() && !songs.isEmpty()) {
                    uploadImageToFirebase();
                } else {
                    Toast.makeText(UploadAlbum.this, "Please select an image, enter an album title, and add at least one song", Toast.LENGTH_SHORT).show();
                }
            }
        });

        navigationView.setNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.menu_uploadAlbum) {
                // Handle settings click
                startActivity(new Intent(UploadAlbum.this, UploadAlbum.class));
            } else if (itemId == R.id.menu_uploadSong) {
                // Handle login click
                startActivity(new Intent(UploadAlbum.this, UploadSong.class));
            } else if (itemId == R.id.menu_profile) {
                // Handle register click
                // Example: startActivity(new Intent(MainActivity.this, RegisterActivity.class));
            }
            else if (itemId == R.id.menu_logout) {
                // Handle register click
                // Example: startActivity(new Intent(MainActivity.this, RegisterActivity.class));

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
    private void openFileChooser(int requestCode) {
        Intent intent = new Intent();
        if (requestCode == PICK_SONG_REQUEST) {
            intent.setType("audio/*");
        } else if (requestCode == PICK_IMAGE_REQUEST) {
            intent.setType("image/*");
        }
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_SONG_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri songUri = data.getData();
            String fileName = getFileName(songUri);
            Song song = new Song();
            song.setSongUrl(songUri.toString()); // Convert Uri to String
            String songTitle = etSongTitle.getText().toString();
            song.setTitle(songTitle);
            songs.add(song);
            songsAdapter.notifyDataSetChanged();
            etSongTitle.setText(""); // Clear the song title field
        } else if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            Glide.with(this).load(imageUri).into(ivAlbumImage);
        }
    }

    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    int columnIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (columnIndex != -1) {
                        result = cursor.getString(columnIndex);
                    }
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    private void uploadImageToFirebase() {
        progressBar.setVisibility(View.VISIBLE);
        String storagePath = "images/" + System.currentTimeMillis() + "-" + getFileName(imageUri);
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(storagePath);

        storageReference.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String downloadUrl = uri.toString();
                                uploadAlbumToFirebase(downloadUrl);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(UploadAlbum.this, "Failed to get download URL: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(UploadAlbum.this, "Failed to upload image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void uploadAlbumToFirebase(String imageUrl) {
        progressBar.setVisibility(View.VISIBLE);

        String uniqueAlbumId = UUID.randomUUID().toString();
        Album album = new Album(
                uniqueAlbumId,
                1, // Replace with actual artist ID
                etAlbumTitle.getText().toString(),
                getCurrentDateTime(), // Assuming getCurrentDateTime() returns Date
                imageUrl,
                1 // Replace with actual genre ID
        );

        albumFirebaseHelper.addData("albums", album);

        uploadSongsToFirebase(imageUrl, uniqueAlbumId);
    }

    private void uploadSongsToFirebase(String imageUrl, String albumId) {
        final int totalSongs = songs.size();
        final int[] uploadedSongsCount = {0};

        for (Song song : songs) {
            String songStoragePath = "songs/" + System.currentTimeMillis() + "-" + getFileName(Uri.parse(song.getSongUrl())); // Parse String to Uri
            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(songStoragePath);

            storageReference.putFile(Uri.parse(song.getSongUrl())) // Parse String to Uri
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String songDownloadUrl = uri.toString();
                                    song.setSongUrl(songDownloadUrl);
                                    String uniqueSongId = UUID.randomUUID().toString();
                                    song.setSongId(uniqueSongId);
                                    song.setUserId(String.valueOf(1)); // Set user ID here
                                    song.setDuration(220); // Set song duration here
                                    song.setReleaseYear(getCurrentDateTime());
                                    song.setPictureSong(imageUrl);
                                    song.setCategoryId(String.valueOf(1)); // Set category ID here
                                    song.setAlbumId(albumId); // Set album ID for the song
                                    songFirebaseHelper.addData("songs", song);
                                    uploadedSongsCount[0]++;
                                    if (uploadedSongsCount[0] == totalSongs) {
                                        progressBar.setVisibility(View.GONE);
                                        Toast.makeText(UploadAlbum.this, "Album uploaded successfully", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(UploadAlbum.this, "Failed to get song download URL: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(UploadAlbum.this, "Failed to upload song: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}
