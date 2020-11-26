package com.example.pole_vault_tracker;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pole_vault_tracker.storage.Local;

public class MainActivity extends AppCompatActivity {
    private final MainActivity thisPointer = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.new_game_btn).setOnClickListener(v -> startEditGameAttributes(Local.insertGame(this)));
        RecyclerView recyclerView = findViewById(R.id.previous_matches_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        GameAdapter gameAdapter = new GameAdapter();
        gameAdapter.submitList(Local.getGames(this));
        gameAdapter.setOnItemClickListener((integer, view) -> startEditGameAttributes(gameAdapter.getCurrentList().get(integer).getId()));
        recyclerView.setAdapter(gameAdapter);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                Local.deleteGame(thisPointer, gameAdapter.getCurrentList().get(viewHolder.getAdapterPosition()).getId());
                gameAdapter.submitList(Local.getGames(thisPointer));
            }
        }).attachToRecyclerView(recyclerView);

    }

    private void startEditGameAttributes(long gameID){
        Local.setActiveGameID(this, gameID);
        startActivity(new Intent(this, EditGameAttributes.class));
    }
}