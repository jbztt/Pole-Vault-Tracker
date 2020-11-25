package com.example.pole_vault_tracker;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pole_vault_tracker.storage.Local;

public class MainActivity extends AppCompatActivity {
    public static final String SHARED_PREFERENCE_KEY_GAME_ID = "GAME_ID";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.new_match_btn).setOnClickListener(v -> {
            getPreferences(Context.MODE_PRIVATE).edit().putLong(SHARED_PREFERENCE_KEY_GAME_ID, Local.insertGame(this)).apply();
            startActivity(new Intent(this, EditMatchAttributes.class));
        });
        RecyclerView lv = findViewById(R.id.previous_matches_list_view);
        lv.setLayoutManager(new LinearLayoutManager(this));
        lv.setItemAnimator(new DefaultItemAnimator());
        lv.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        GameAdapter gameAdapter = new GameAdapter();
        gameAdapter.submitList(Local.getGames(this));
        lv.setAdapter(gameAdapter);

    }
}