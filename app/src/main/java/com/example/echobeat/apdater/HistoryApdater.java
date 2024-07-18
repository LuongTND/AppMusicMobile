package com.example.echobeat.apdater;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.echobeat.modelFirebase.History;
import com.example.echobeat.R;

import java.util.List;

public class HistoryApdater extends RecyclerView.Adapter<HistoryApdater.HistoryViewHolder> {

    private final List<History> historyList;
    private final Context context;
    private OnItemClickListener listener;

    public HistoryApdater(Context context, List<History> historyList) {
        this.context = context;
        this.historyList = historyList;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.history_item_layout, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        History history = historyList.get(position);

        holder.historyTitle.setText(history.getTitle());

        Glide.with(context)
                .load(history.getCoverImage()) // URL của ảnh từ Firebase
                .apply(new RequestOptions()
                        .placeholder(R.drawable.ic_loading) // Hình ảnh thay thế khi đang tải
                        .error(R.drawable.ic_user)) // Hình ảnh thay thế khi tải lỗi
                .into(holder.historyImage);
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    public class HistoryViewHolder extends RecyclerView.ViewHolder {
        ImageView historyImage;
        TextView historyTitle;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            historyImage = itemView.findViewById(R.id.history_image);
            historyTitle = itemView.findViewById(R.id.history_title);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && listener != null) {
                        listener.onItemClick(historyList.get(position));
                    }
                }
            });
        }
    }

    public void setOnItemClickListener(HistoryApdater.OnItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(History history);
    }
}
