package com.example.pole_vault_tracker;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pole_vault_tracker.storage.model.Game;

import java.util.function.BiConsumer;

public class GameAdapter extends ListAdapter<Game, GameAdapter.GameViewHolder> {

    private BiConsumer<Integer, View> onItemClickListener;

    public GameAdapter() {
        super(Game.DIFF_CALLBACK);
    }

    @NonNull
    @Override
    public GameViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        GameViewHolder gameViewHolder = new GameViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.game_list_row, parent, false));
        gameViewHolder.itemView.setOnClickListener(v -> this.onItemClickListener.accept(gameViewHolder.getAdapterPosition(), v));
        return gameViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull GameViewHolder holder, int position) {
        Game game = getItem(position);
        if (game != null) holder.bindTo(game);
    }

    public void setOnItemClickListener(BiConsumer<Integer, View> onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    static class GameViewHolder extends RecyclerView.ViewHolder {
        private final TextView playerName;
        private final TextView location;

        public GameViewHolder(@NonNull View itemView) {
            super(itemView);
            playerName = itemView.findViewById(R.id.game_list_row_player_name_text_view);
            location = itemView.findViewById(R.id.game_list_row_location_text_view);
        }

        void bindTo(Game game) {
            String playerName = game.getPlayerName();
            if (playerName != null && !playerName.equals(""))
                this.playerName.setText(game.getPlayerName());

            String location = game.getLocation();
            if (location != null && !location.equals(""))
                this.location.setText(game.getLocation());
        }
    }
}
