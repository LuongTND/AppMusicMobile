package com.example.echobeat.activity;

import android.annotation.SuppressLint;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.echobeat.R;

import com.example.echobeat.dbFirebase.FirebaseHelper;
import com.example.echobeat.modelFirebase.Song;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PlayerActivity extends AppCompatActivity {

    private TextView songTitleTextView;
    private ImageView songImageView;
    private ImageButton buttonPlay;
    private ImageButton buttonShuffle;
    private boolean isPlaying = false;
    private MediaPlayer mediaPlayer;
    private String songUrl;
    private int currentPosition = 0; // Variable to keep track of the current position
    private ImageButton buttonNext;
    private ImageButton buttonPrevious;
    private SeekBar playerSeeBarTime;
    private TextView textCurrentTime;
    private TextView textTotalTime;
    private FirebaseHelper<Song> firebaseHelper;

    private List<Song> nextSongsList = new ArrayList<>();
    private int currentSongIndex = 0;
    private boolean isShuffling = false;
    private List<Song> randomSongsList = new ArrayList<>();
    private int currentRandomIndex = 0;
    private ImageButton buttonRepeat;
    private boolean isRepeating = false;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        songTitleTextView = findViewById(R.id.song_title);
        songImageView = findViewById(R.id.imageSong);
        buttonPlay = findViewById(R.id.buttonPlay);
        buttonNext = findViewById(R.id.buttonNext);
        buttonPrevious = findViewById(R.id.buttonPrevious);
        buttonShuffle = findViewById(R.id.buttonShuffle);
        playerSeeBarTime = findViewById(R.id.playerSeeBarTime);
        textCurrentTime = findViewById(R.id.textCurrentTime);
        textTotalTime = findViewById(R.id.textTotalTime);
        firebaseHelper = new FirebaseHelper<>();

        // Get song data from Intent
        Song song = getIntent().getParcelableExtra("SONG_DATA");
        songUrl = getIntent().getStringExtra("SONG_URL");

        // Display song information
        if (song != null) {
            songTitleTextView.setText(song.getTitle());
            Log.d("PlayerActivity", "Song clicked: " + songUrl);

            // Load image using Glide
            Glide.with(this)
                    .load(song.getPictureSong()) // URL of the image from Firebase
                    .apply(new RequestOptions()
                            .placeholder(R.drawable.ic_loading) // Placeholder image
                            .error(R.drawable.ic_error)) // Error image
                    .into(songImageView);
        }

        // Initialize MediaPlayer
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnPreparedListener(mp -> {
            // Ready to play music
            mediaPlayer.seekTo(currentPosition); // Seek to the last position
            mediaPlayer.start();

            int duration = mediaPlayer.getDuration();
            textTotalTime.setText(millisecondsToTimer(duration));
            playerSeeBarTime.setMax(duration);

            // Update current time and seek bar progress
            updateSeekBar();
        });

        mediaPlayer.setOnCompletionListener(mp -> {
            if (randomSongsList.isEmpty() || currentRandomIndex >= randomSongsList.size()) {
                // Lấy bộ 5 bài hát ngẫu nhiên mới từ Firebase
                firebaseHelper.getRandomSongs(5, new FirebaseHelper.DataCallback<Song>() {
                    @Override
                    public void onCallback(List<Song> data) {
                        if (data != null && !data.isEmpty()) {
                            randomSongsList.clear();
                            randomSongsList.addAll(data);
                            currentRandomIndex = 0;
                            playRandomSongs();
                        } else {
                            Log.e("PlayerActivity", "No random songs loaded from Firebase");
                            // Xử lý trường hợp không tìm thấy bài hát ngẫu nhiên
                        }
                    }
                });
            } else {
                // Tiếp tục phát các bài hát còn lại trong RandomSongsList
                playRandomSongs();
            }
        });

        buttonPlay.setOnClickListener(v -> {
            if (songUrl != null) {
                if (isPlaying) {
                    // Pause playback
                    mediaPlayer.pause();
                    pausePlayback();
                } else {
                    // Start or resume playback
                    try {
                        if (currentPosition > 0) {
                            mediaPlayer.seekTo(currentPosition); // Seek to last position if paused
                            mediaPlayer.start();
                            startPlayback();
                        } else {
                            mediaPlayer.reset();
                            mediaPlayer.setDataSource(songUrl);
                            mediaPlayer.prepareAsync(); // Prepare asynchronously
                            startPlayback();
                        }
                    } catch (IOException e) {
                        Log.e("PlayerActivity", "Error setting data source", e);
                    }
                }
            } else {
                // Handle null song or song URL case
                Log.e("PlayerActivity", "Song or song URL is null");
                // Display an error message or handle accordingly
            }
        });

        buttonShuffle.setOnClickListener(v -> {
            isShuffling = !isShuffling;
            if (isShuffling) {
                buttonShuffle.setImageResource(R.drawable.baseline_shuffle);
                // Fetch initial set of random songs
                fetchRandomSongs();
                buttonRepeat.setEnabled(false);
            } else {
                buttonShuffle.setImageResource(R.drawable.baseline_shuffleoff);
                buttonRepeat.setEnabled(true);
            }
        });

        buttonRepeat = findViewById(R.id.buttonRepeat);
        buttonRepeat.setOnClickListener(v -> {
            isRepeating = !isRepeating;
            if (isRepeating) {
                buttonRepeat.setImageResource(R.drawable.baseline_repeatone);
                mediaPlayer.setLooping(true);
                buttonShuffle.setEnabled(false);
            } else {
                buttonRepeat.setImageResource(R.drawable.baseline_repeat);
                mediaPlayer.setLooping(false);
                buttonShuffle.setEnabled(true);
            }
        });

        // Handle seek bar change
        playerSeeBarTime.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mediaPlayer.seekTo(progress);
                    playerSeeBarTime.setProgress(progress);
                    textCurrentTime.setText(millisecondsToTimer(progress));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        buttonNext.setOnClickListener(v -> {
            playNextSong();
        });

        buttonPrevious.setOnClickListener(v -> {
            playPreviousSong();
        });
    }

    private void fetchRandomSongs() {
        firebaseHelper.getRandomSongs(5, new FirebaseHelper.DataCallback<Song>() {
            @Override
            public void onCallback(List<Song> data) {
                if (data != null && !data.isEmpty()) {
                    randomSongsList.clear();
                    randomSongsList.addAll(data);
                    currentRandomIndex = 0;
                    Collections.shuffle(randomSongsList);
                    playRandomSongs();
                } else {
                    Log.e("PlayerActivity", "No random songs loaded from Firebase");
                }
            }
        });
    }

    private void playRandomSongs() {
        if (!randomSongsList.isEmpty() && currentRandomIndex < randomSongsList.size()) {
            Song nextSong = randomSongsList.get(currentRandomIndex);
            songTitleTextView.setText(nextSong.getTitle());
            Glide.with(PlayerActivity.this)
                    .load(nextSong.getPictureSong())
                    .apply(new RequestOptions()
                            .placeholder(R.drawable.ic_loading)
                            .error(R.drawable.ic_error))
                    .into(songImageView);
            songUrl = nextSong.getSongUrl();
            currentPosition = 0;
            mediaPlayer.reset();
            try {
                mediaPlayer.setDataSource(songUrl);
                mediaPlayer.prepareAsync();
            } catch (IOException e) {
                Log.e("PlayerActivity", "Error setting data source for next song", e);
            }
            currentRandomIndex++;
        } else {
            // Handle end of list or no songs available
            Log.e("PlayerActivity", "No more random songs to play in the list");
            // Optionally reset or handle end of list behavior
        }
    }

    private void playNextSong() {
        if (currentSongIndex >= nextSongsList.size()) {
            // Fetch new set of 5 random songs
            firebaseHelper.getRandomSongs(5, new FirebaseHelper.DataCallback<Song>() {
                @Override
                public void onCallback(List<Song> data) {
                    if (data != null && !data.isEmpty()) {
                        nextSongsList.clear();
                        nextSongsList.addAll(data);
                        currentSongIndex = 0; // Reset index to start from the new list
                        playNextSong(); // Start playing the next song in the new list
                    } else {
                        Log.e("PlayerActivity", "No next songs loaded from Firebase");
                        // Handle case where no next songs are found
                    }
                }
            });
        } else {
            // Play next song in the list
            if (currentSongIndex < nextSongsList.size()) {
                Song nextSong = nextSongsList.get(currentSongIndex);
                songTitleTextView.setText(nextSong.getTitle());
                Glide.with(PlayerActivity.this)
                        .load(nextSong.getPictureSong())
                        .apply(new RequestOptions()
                                .placeholder(R.drawable.ic_loading)
                                .error(R.drawable.ic_error))
                        .into(songImageView);
                songUrl = nextSong.getSongUrl();
                currentPosition = 0;
                mediaPlayer.reset();
                try {
                    mediaPlayer.setDataSource(songUrl);
                    mediaPlayer.prepareAsync();
                } catch (IOException e) {
                    Log.e("PlayerActivity", "Error setting data source for next song", e);
                }
                currentSongIndex++;
            }
        }
        //Đặt chế độ vòng lặp dựa trên trạng thái hiện tại
        if (isRepeating) {
            mediaPlayer.setLooping(true);
        } else {
            mediaPlayer.setLooping(false);
        }
    }

    private void playPreviousSong() {
        if (currentSongIndex <= 0) {
            // Fetch new set of 5 random songs for previous songs
            firebaseHelper.getRandomSongs(5,  new FirebaseHelper.DataCallback<Song>() {
                @Override
                public void onCallback(List<Song> data) {
                    if (data != null && !data.isEmpty()) {
                        nextSongsList.clear();
                        nextSongsList.addAll(data);
                        currentSongIndex = nextSongsList.size() - 1; // Start from the end of the new list
                        playPreviousSong(); // Start playing the previous song in the new list
                    } else {
                        Log.e("PlayerActivity", "No previous songs loaded from Firebase");
                        // Handle case where no previous songs are found
                    }
                }
            });
        } else {
            // Play previous song in the list
            if (currentSongIndex > 0) {
                currentSongIndex--; // Decrement index first
                Song previousSong = nextSongsList.get(currentSongIndex);
                songTitleTextView.setText(previousSong.getTitle());
                Glide.with(PlayerActivity.this)
                        .load(previousSong.getPictureSong())
                        .apply(new RequestOptions()
                                .placeholder(R.drawable.ic_loading)
                                .error(R.drawable.ic_error))
                        .into(songImageView);
                songUrl = previousSong.getSongUrl();
                currentPosition = 0;
                mediaPlayer.reset();
                try {
                    mediaPlayer.setDataSource(songUrl);
                    mediaPlayer.prepareAsync();
                } catch (IOException e) {
                    Log.e("PlayerActivity", "Error setting data source for previous song", e);
                }
            }
        }
        //Đặt chế độ vòng lặp dựa trên trạng thái hiện tại
        if (isRepeating) {
            mediaPlayer.setLooping(true);
        } else {
            mediaPlayer.setLooping(false);
        }
    }

    private void startPlayback() {
        // Start playback logic
        isPlaying = true;
        buttonPlay.setImageResource(R.drawable.baseline_pause);
        updateSeekBar(); // Start updating SeekBar
    }

    private void pausePlayback() {
        // Pause playback logic
        isPlaying = false;
        currentPosition = mediaPlayer.getCurrentPosition(); // Save the current position
        buttonPlay.setImageResource(R.drawable.baseline_play);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        isPlaying = false; // Stop the updateSeekBar thread
    }

    // Helper method to update SeekBar progress and current time
    private void updateSeekBar() {
        new Thread(() -> {
            while (mediaPlayer != null && mediaPlayer.isPlaying()) {
                try {
                    runOnUiThread(() -> {
                        int currentPosition = mediaPlayer.getCurrentPosition();
                        playerSeeBarTime.setProgress(currentPosition);
                        textCurrentTime.setText(millisecondsToTimer(currentPosition));
                    });
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    // Helper method to convert milliseconds to time format (mm:ss)
    private String millisecondsToTimer(long milliseconds) {
        String timerString = "";
        String secondsString;

        int hours = (int) (milliseconds / (1000 * 60 * 60));
        int minutes = (int) (milliseconds % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) ((milliseconds % (1000 * 60 * 60)) % (1000 * 60) / 1000);

        if (hours > 0) {
            timerString = hours + ":";
        }

        if (seconds < 10) {
            secondsString = "0" + seconds;
        } else {
            secondsString = "" + seconds;
        }

        timerString = timerString + minutes + ":" + secondsString;
        return timerString;
    }
}
