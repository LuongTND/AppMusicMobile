package com.example.echobeat.apdater;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.echobeat.R;
import com.example.echobeat.modelFirebase.Playlist;
import com.example.echobeat.modelFirebase.Song;
import com.example.echobeat.modelSqlite.ListPlaylist;
import com.example.echobeat.repository.SongRepository;

import java.util.List;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.ViewHolder> {

    private final Context context;
    private final List<Song> songs;

    private OnItemClickListener listener;
    private final SongRepository songRepository;

    public SongAdapter(Context context, List<Song> songs) {
        this.context = context;
        this.songs = songs;
        this.songRepository = new SongRepository(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_song_horizontal, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Song song = songs.get(position);

        holder.songTitle.setText(song.getTitle());

        // Load image from URL using Glide
        Glide.with(context)
                .load(song.getPictureSong()) // URL of the image from Firebase
                .apply(new RequestOptions()
                        .placeholder(R.drawable.ic_loading) // Placeholder image while loading
                        .error(R.drawable.ic_error)) // Error image if loading fails
                .into(holder.songImage);

        // Handle button click
        holder.btnAddToPlaylist.setOnClickListener(v -> {
            songRepository.addSongToPlaylist(song);
            if (listener != null) {
                listener.onItemClick(song);
            }
            Toast.makeText(context, "Added to Playlist", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView songTitle;
        ImageView songImage;
        Button btnAddToPlaylist;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            songTitle = itemView.findViewById(R.id.song_title);
            songImage = itemView.findViewById(R.id.song_image);
            btnAddToPlaylist = itemView.findViewById(R.id.btnAddToPlaylist);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && listener != null) {
                        listener.onItemClick(songs.get(position));
                    }
                }
            });
        }
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(Song song);
    }
}