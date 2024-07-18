package com.example.echobeat.dbFirebase;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.echobeat.modelFirebase.Album;
import com.example.echobeat.modelFirebase.Artist;
import com.example.echobeat.modelFirebase.Category;
import com.example.echobeat.modelFirebase.History;
import com.example.echobeat.modelFirebase.Playlist;
import com.example.echobeat.modelFirebase.Song;
import com.example.echobeat.modelFirebase.User;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class SeedData {

    private final  FirebaseHelper<Album> albumHelper;
    private final FirebaseHelper<Artist> artistHelper;
    private final FirebaseHelper<Category> categoryHelper;
    private final FirebaseHelper<Playlist> playlistHelper;
    private final FirebaseHelper<Song> songHelper;
    private final FirebaseHelper<User> userHelper;
    private final FirebaseHelper<History> historyHelper;
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";


    public SeedData() {
        albumHelper = new FirebaseHelper<>();
        artistHelper = new FirebaseHelper<>();
        categoryHelper = new FirebaseHelper<>();
        playlistHelper = new FirebaseHelper<>();
        songHelper = new FirebaseHelper<>();
        userHelper = new FirebaseHelper<>();
        historyHelper = new FirebaseHelper<>();
    }

    public void seedAllData() {
//        seedAlbums(10); // Truyền số lượng album cần tạo vào đây
//        seedArtists(15); // Truyền số lượng nghệ sĩ cần tạo vào đây
//        seedCategories(8); // Truyền số lượng danh mục cần tạo vào đây
//        seedPlaylists(10); // Truyền số lượng playlist cần tạo vào đây
//        seedSongs(5); // Truyền số lượng bài hát cần tạo vào đây
//        seedUsers(10); // Truyền số lượng người dùng cần tạo vào đây
//        seedHistory(100);
    }

    private void seedHistory(int count) {
        for (int i = 1; i <= count; i++) {
            String historyId = String.valueOf(i);
            String type;
            String[] itemId = new String[1000]; // Increase size to a reasonable number
            String[] title = new String[1000]; // Increase size to a reasonable number
            String[] coverImage = new String[1000]; // Increase size to a reasonable number

            // Random loại (song, album, playlist)
            int itemType = new Random().nextInt(2);
            if (itemType == 0) {
                type = "song";
                songHelper.getRandomSong(new FirebaseHelper.SongCallback() {
                    @Override
                    public void onSongLoaded(Song song) {
                        if (song != null) {
                            int randomIndex = new Random().nextInt(5); // Use the same size as arrays
                            itemId[randomIndex] = song.getSongId();
                            title[randomIndex] = song.getTitle();
                            coverImage[randomIndex] = song.getPictureSong();
                            History history = new History(historyId, type, itemId[randomIndex], title[randomIndex], coverImage[randomIndex], getRandomDate());
                            historyHelper.addData("histories", history);
                        } else {
                            // Handle case where song is null
                            Log.d("SeedData", "No song on firebase");
                        }
                    }
                });
            } else {
                type = "album";
                albumHelper.getRandomAlbum(new FirebaseHelper.AlbumCallback() {
                    @Override
                    public void onAlbumLoaded(Album album) {
                        if (album != null) {
                            int randomIndex = new Random().nextInt(15); // Use the same size as arrays
                            itemId[randomIndex] = album.getAlbumId();
                            title[randomIndex] = album.getTitle();
                            coverImage[randomIndex] = album.getCoverImage();
                            History history = new History(historyId, type, itemId[randomIndex], title[randomIndex], coverImage[randomIndex], getRandomDate());
                            historyHelper.addData("histories", history);
                        } else {
                            // Handle case where album is null
                            Log.d("SeedData", "No album on firebase");
                        }
                    }
                });
            }
        }
    }


    private void seedAlbums(int count) {
        for (int i = 1; i <= count; i++) {
            String albumId = i+"";
            int userId = getRandomUserId();
            String albumName = generateRandomName(3) + " " + generateRandomName(3);

            String albumImage = "https://vignette.wikia.nocookie.net/leagueoflegends/images/a/a6/Jax_OriginalCentered.jpg/revision/latest/scale-to-width-down/1215?cb=20180414203245";
            int categoryId = getRandomCategoryId();

            Date releaseYear = getRandomDate();

            Album album = new Album(albumId, userId, albumName, releaseYear, albumImage, categoryId);
            albumHelper.addData("albums", album);
        }
    }

    public static @NonNull Date getRandomDate() {
        Random random = new Random();
        Calendar startCalendar = Calendar.getInstance();
        startCalendar.set(2023, Calendar.JANUARY, 1); // Set start date to January 1, 2023
        long startMillis = startCalendar.getTimeInMillis();
        Calendar endCalendar = Calendar.getInstance(); // Current date
        long endMillis = endCalendar.getTimeInMillis();
        // Generate random date between start date and end date
        long randomMillis = startMillis + (long) (random.nextDouble() * (endMillis - startMillis));
        Date releaseYear = new Date(randomMillis);
        return releaseYear;
    }
    public static @NonNull Date getCurrentDateTime() {
        Calendar calendar = Calendar.getInstance();
        Date currentDate = calendar.getTime();
        return currentDate;
    }

    private void seedArtists(int count) {
        for (int i = 1; i <= count; i++) {
            int artistId = i;
            String userName = generateRandomName(10); // Tạo tên nghệ sĩ ngẫu nhiên với độ dài 10
            String email = "artist" + artistId + "@example.com";
            String artistImage = "https://scontent.fdad3-6.fna.fbcdn.net/v/t1.6435-9/79315938_783274145470099_8800946627310256128_n.jpg?_nc_cat=111&ccb=1-7&_nc_sid=53a332&_nc_ohc=zSKOv4nh2twQ7kNvgH31r0r&_nc_ht=scontent.fdad3-6.fna&oh=00_AYAH1JZ5T-ZhAaaL-QBzYPUITdIiGkAIoLbICToy53CXdw&oe=66B040EA";

            int userId = getRandomUserId();
            String artistName =generateRandomName(10);;
            String bio = "Artist " + artistId + " bio";
            List<String> songIds = Arrays.asList(String.valueOf(i), String.valueOf(i + 1), String.valueOf(i + 2)); // Example songIds
            String genre = (i % 2 == 0) ? "Rock" : "Pop";

            Artist artist = new Artist(artistId + "", userName, email, artistImage, 1, "asdhjfgahjsdgfhjsadgfhjasg", userId + "",artistName, bio, songIds, genre);
            artistHelper.addData("artists", artist);
        }
    }

    private String generateRandomName(int length) {
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(CHARACTERS.length());
            sb.append(CHARACTERS.charAt(index));
        }
        return sb.toString();
    }

    private void seedCategories(int count) {
        for (int i = 1; i <= count; i++) {
            Category category = new Category(i, "Category " + i);
            categoryHelper.addData("categories", category);
        }
    }



    private void seedPlaylists(int count) {
        for (int i = 1; i <= count; i++) {
            String playlistId = i + "";
            int userId = getRandomUserId();
            String playlistName = "Playlist " + playlistId;
            String playlistDescription = "Playlist description " + playlistId;
            String playlistImage = "playlist" + playlistId + ".jpg";

            Playlist playlist = new Playlist(playlistId, userId, playlistName, playlistDescription, playlistImage);
            playlistHelper.addData("playlists", playlist);
        }
    }

//    private void seedSongs(int count) {
//        Random random = new Random();
//
//        for (int i = 1; i <= count; i++) {
//            String userId = getRandomUserIdAsString();
//            String songUrl = "https://firebasestorage.googleapis.com/v0/b/echobeat-bbd1f.appspot.com/o/dung-lam-trai-tim-anh-dau-piano-khong-loi.mp3?alt=media&token=7528e94b-7f53-4b8a-8c14-f623c1d52c37";
//            String songTitle = generateRandomName(3) + " " +generateRandomName(3) + " " + generateRandomName(3);
//            int songDuration = 180 + (i * 10); // incrementing duration
//            Date releaseYear = getRandomDate();
//            String pictureSong = "https://th.bing.com/th/id/OIP.4Acoxt6K25NRPbtIVvxmQQAAAA?rs=1&pid=ImgDetMain";
//            String categoryId = getRandomCategoryIdAsString();
//
//            // Generate random play count between 0 and 1000
//            int playCount = random.nextInt(1001); // 0 to 1000
//
//            Song song = new Song(playCount, String.valueOf(i), userId, songUrl, songTitle, songDuration, releaseYear, pictureSong, categoryId, album);
//            songHelper.addData("songs", song);
//        }
//    }



    private void seedUsers(int count) {
        for (int i = 1; i <= count; i++) {
            User user = new User(i+"", ("User " + i), ("User" + i + "@example.com"), "https://th.bing.com/th/id/OIP.4Acoxt6K25NRPbtIVvxmQQAAAA?rs=1&pid=ImgDetMain", getRandomRoleId(), "sdhgfahsdgfhjagsdfhjgsadhjfggh");
            userHelper.addData("users", user);
        }
    }




    private int getRandomUserId() {
        Random random = new Random();
        int randomUserId = random.nextInt(10) + 1; // Assuming user IDs range from 1 to 10
        return randomUserId;
    }



    private String getRandomUserIdAsString() {
        return String.valueOf(getRandomUserId());
    }

    private int getRandomCategoryId() {
        Random random = new Random();
        int randomCategoryId = random.nextInt(8) + 1; // Assuming category IDs range from 1 to 8
        return randomCategoryId;
    }

    private String getRandomCategoryIdAsString() {
        return String.valueOf(getRandomCategoryId());
    }

    private int getRandomRoleId() {
        Random random = new Random();
        int randomCategoryId = random.nextInt(2) + 1; // Assuming category IDs range from 1 to 8
        return randomCategoryId;
    }

}
