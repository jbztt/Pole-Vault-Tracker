package com.example.pole_vault_tracker;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pole_vault_tracker.storage.Local;
import com.example.pole_vault_tracker.storage.Remote;
import com.example.pole_vault_tracker.storage.model.Game;
import com.example.pole_vault_tracker.storage.model.Jump;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import java.sql.SQLException;
import java.util.List;

public class FullHistory extends AppCompatActivity {
    private final FullHistory thisPointer = this;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_history);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        CollapsingToolbarLayout toolBarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        toolBarLayout.setTitle(getTitle());

        handler = new Handler(Looper.myLooper());

        RecyclerView recyclerView = findViewById(R.id.full_history_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        GameAdapter gameAdapter = new GameAdapter();
        new Thread(() -> {
            try {
                List<Game> games = Remote.getGames(Remote.getTenantName(this));
                handler.post(() -> gameAdapter.submitList(games));
            } catch (SQLException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }).start();
        gameAdapter.setOnItemClickListener((integer, view) -> startEditGameAttributes(gameAdapter.getCurrentList().get(integer)));
        recyclerView.setAdapter(gameAdapter);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                new Thread(() -> {
                    try {
                        Remote.deleteGame(gameAdapter.getCurrentList().get(viewHolder.getAdapterPosition()).getId());
                        List<Game> games = Remote.getGames(Remote.getTenantName(thisPointer));
                        handler.post(() -> gameAdapter.submitList(games));

                    } catch (SQLException | ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }).start();
            }
        }).attachToRecyclerView(recyclerView);
    }

    private void startEditGameAttributes(Game game) {
        Local.setActiveGameID(this, game.getId());
        new Thread(() -> {
            if (Local.getGame(this, game.getId()) == null) {
                Local.updateGame(this, Local.insertGame(this), game);
            }
            try {
                for (Jump jump : Remote.getJumps(game.getId())) {
                    Local.updateJump(this, Local.insertJump(this, game.getId()), jump);
                }
            } catch (SQLException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }).start();
        startActivity(new Intent(this, EditGameAttributes.class));
    }
}