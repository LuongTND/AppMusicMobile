package com.example.echobeat.activity;

import static com.example.echobeat.dbFirebase.SeedData.getCurrentDateTime;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bumptech.glide.Glide;
import com.example.echobeat.MainActivity;
import com.example.echobeat.R;
import com.example.echobeat.activity.loginModel.OptionRole;
import com.example.echobeat.dbFirebase.FirebaseHelper;
import com.example.echobeat.modelFirebase.Song;
import com.example.echobeat.repository.UserRepository;
import com.example.echobeat.session.SessionManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

public class UploadSong extends AppCompatActivity {

    private static final int PICK_SONG_REQUEST = 1;
    private static final int PICK_IMAGE_REQUEST = 2;
    private Button btnSelectSong, btnUploadSong, btnUploadImage;
    private TextView tvSelectedSong;
    private EditText etSongTitle;
    private ProgressBar progressBar;
    private ImageView ivSongImage;
    private Uri songUri;
    private Uri imageUri;
    private FirebaseHelper<Song> firebaseHelper;

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private NavigationView navigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_song);

        btnSelectSong = findViewById(R.id.btnSelectSong);
        btnUploadSong = findViewById(R.id.btnUploadSong);
        btnUploadImage = findViewById(R.id.btnUploadImage);
        tvSelectedSong = findViewById(R.id.tvSelectedSong);
        etSongTitle = findViewById(R.id.etSongTitle);
        progressBar = findViewById(R.id.progressBar);
        ivSongImage = findViewById(R.id.ivSongImage);
        drawerLayout = findViewById(R.id.drawer_artist_uploadSong);
        navigationView = findViewById(R.id.nav_view_artist);
        setupDrawer();

        firebaseHelper = new FirebaseHelper<>();

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

        btnUploadSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (songUri != null && imageUri != null && !etSongTitle.getText().toString().isEmpty()) {
                    uploadImageToFirebase();
                } else {
                    Toast.makeText(UploadSong.this, "Please select a song, an image, and enter a title", Toast.LENGTH_SHORT).show();
                }
            }
        });

        navigationView.setNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.menu_uploadAlbum) {
                // Handle settings click
                startActivity(new Intent(UploadSong.this, UploadAlbum.class));
            } else if (itemId == R.id.menu_uploadSong) {
                // Handle login click
                startActivity(new Intent(UploadSong.this, UploadSong.class));
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
            songUri = data.getData();
            String fileName = getFileName(songUri);
            tvSelectedSong.setText(fileName);
            btnUploadSong.setEnabled(true);
        } else if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            Glide.with(this).load(imageUri).into(ivSongImage);
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
                                uploadSongToFirebase(downloadUrl);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(UploadSong.this, "Failed to get download URL: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(UploadSong.this, "Failed to upload image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void uploadSongToFirebase(String imageUrl) {
        String songStoragePath = "songs/" + System.currentTimeMillis() + "-" + getFileName(songUri);
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(songStoragePath);

        storageReference.putFile(songUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String songDownloadUrl = uri.toString();
                                Song song = new Song();
                                song.setSongUrl(songDownloadUrl);
                                //create song id
                                String uniqueSongId = UUID.randomUUID().toString();
                                song.setSongId(uniqueSongId);
                                //get user id from session
//                                SessionManager sessionManager = new SessionManager(UploadSong.this);
//                                String googleId = sessionManager.getGoogleId();
//                                String userId = userRepository.getUserIdByGoogleId(googleId);
                                song.setUserId(String.valueOf(1));
                                song.setTitle(etSongTitle.getText().toString());
                                song.setDuration(220);
                                //get the current datetime
                                song.setReleaseYear(getCurrentDateTime());
                                song.setPictureSong(imageUrl);
                                song.setCategoryId(String.valueOf(1));
                                firebaseHelper.addData("songs", song);
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(UploadSong.this, "Song uploaded successfully", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(UploadSong.this, "Failed to get song download URL: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(UploadSong.this, "Failed to upload song: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
