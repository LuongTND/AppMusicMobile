package com.example.echobeat.activity.loginModel;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.echobeat.MainActivity;
import com.example.echobeat.R;
import com.example.echobeat.dbFirebase.FirebaseHelper;
import com.example.echobeat.modelSqlite.User;
import com.example.echobeat.repository.ArtistRepository;
import com.example.echobeat.repository.UserRepository;
import com.example.echobeat.session.SessionManager;
import com.example.echobeat.modelSqlite.Artist;

import java.util.Arrays;
import java.util.List;


public class AddInfomation extends AppCompatActivity {

    private ImageButton back;
    private EditText et_name;

    private MultiAutoCompleteTextView et_biography;

    private Spinner spinnerMusicGenre;
    private Button next;
    private ArtistRepository artistRepository;
    private UserRepository userRepository;
    private FirebaseHelper<com.example.echobeat.modelFirebase.User> userHelper;
    private FirebaseHelper<com.example.echobeat.modelFirebase.User> artistHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_infomation);

        back = findViewById(R.id.btn_back);
        back.setOnClickListener(v -> { Intent intent = new Intent(AddInfomation.this, OptionRole.class);startActivity(intent);});

        // Initialize repositories
        artistRepository = new ArtistRepository(this);
        userRepository = new UserRepository(this);

        // Initialize UI components
        back = findViewById(R.id.btn_back);
        et_name = findViewById(R.id.et_name);
        et_biography = findViewById(R.id.et_biography);
        next = findViewById(R.id.btn_next);

        // Set up Spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.music_genres_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMusicGenre.setAdapter(adapter);

        SessionManager sessionManager = new SessionManager(this);
        String googleId = sessionManager.getGoogleId();
        String userId = sessionManager.getUserid();
        String userName = sessionManager.getUsername();
        String email = sessionManager.getEmail();
        String image = sessionManager.getImage();
        int roleId =sessionManager.getRoleId();

        // Set up back button listener
        back.setOnClickListener(v -> {
            Intent intent = new Intent(AddInfomation.this, OptionRole.class);
            startActivity(intent);
        });

        // Set up next button listener
        next.setOnClickListener(v -> {
            // Get input values
            String name = et_name.getText().toString().trim();
            String biography = et_biography.getText().toString().trim();
            String selectedGenre = spinnerMusicGenre.getSelectedItem().toString();

            // Validate input
            if (name.isEmpty() || biography.isEmpty()||selectedGenre.isEmpty()) {
                Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
                return;
            }
            //luu user vao sql lite
            User userInfo = new User(userId,userName, email, image, 2, googleId);
            UserRepository userRepository = new UserRepository(getApplicationContext());
            userRepository.register(userInfo);
            //luu user vao firebase
            com.example.echobeat.modelFirebase.User userfb = new com.example.echobeat.modelFirebase.User(userId,userName, email, image, 2, googleId);
            userHelper.addData("users", userfb);
            //cap nhat roleId o session
            int newRoleId = 2;
            sessionManager.updateRoleId(newRoleId);

            // Create a new Artist object
            Artist artist = new Artist();
            artist.setUserId(userId);
            artist.setArtistName(name);
            artist.setBiography(biography);
            //luu ca si vao fire base
            String artistName =name;
            String bio = biography;
            List<String> songIds = null;
            String genre =selectedGenre;
            com.example.echobeat.modelFirebase.Artist artistfb = new com.example.echobeat.modelFirebase.Artist(userId + "", userName, email, image, 2, googleId, userId + "",artistName, bio, songIds, genre);
            artistHelper.addData("artists", artistfb);

            // luu ca si vao sql lite
            boolean isSaved = artistRepository.saveArtist(artist);
            if (isSaved) {
                    Toast.makeText(this, "Artist information saved successfully!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(AddInfomation.this, MainActivity.class);
                    startActivity(intent);
            } else {
                Toast.makeText(this, "Failed to save artist information!", Toast.LENGTH_SHORT).show();
            }

        });


    }
}
