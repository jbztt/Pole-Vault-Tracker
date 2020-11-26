package com.example.pole_vault_tracker;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ScrollingTabContainerView;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pole_vault_tracker.storage.Common;
import com.example.pole_vault_tracker.storage.Local;
import com.example.pole_vault_tracker.storage.Remote;

import java.sql.SQLException;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private final MainActivity thisPointer = this;
    private EditText loggedInAsEditText;
    private final GameAdapter gameAdapter = new GameAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.new_game_btn).setOnClickListener(v -> {
            long gameID = Local.insertGame(this);
            new Thread(() -> {
                try {
                    Remote.insertGame( gameID, Remote.getTenantName(this));
                } catch (SQLException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }).start();
            startEditGameAttributes(gameID);
        });
        RecyclerView recyclerView = findViewById(R.id.previous_games_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

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
                long gameID = gameAdapter.getCurrentList().get(viewHolder.getAdapterPosition()).getId();
                Local.deleteGame(thisPointer, gameID);
                gameAdapter.submitList(Local.getGames(thisPointer));
                new Thread(() -> {
                    try {
                        Remote.deleteGame(gameID);
                    } catch (SQLException | ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }).start();
            }
        }).attachToRecyclerView(recyclerView);
        findViewById(R.id.history_btn).setOnClickListener(v -> {
            Remote.setTenantName(this, loggedInAsEditText.getText().toString());
            startActivity(new Intent(this, FullHistory.class));
        });
        loggedInAsEditText = findViewById(R.id.logged_in_as_edit_text);
        loggedInAsEditText.setText(Remote.getTenantName(this));

        Spinner spinner = findViewById(R.id.languages_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.languages_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(Common.getLocaleID(this));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == Common.getLocaleID(thisPointer)) return;
                if (position == 0) {
                    Locale.setDefault(Locale.FRENCH);
                    setAppLocale("fr");
                }
                else {
                    Locale.setDefault(Locale.ENGLISH);
                    setAppLocale("en");
                }
                Common.setLocaleID(thisPointer, position);
                thisPointer.recreate();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        gameAdapter.submitList(Local.getGames(this));
    }

    private void startEditGameAttributes(long gameID) {
        Remote.setTenantName(this, loggedInAsEditText.getText().toString());
        Local.setActiveGameID(this, gameID);
        startActivity(new Intent(this, EditGameAttributes.class));
    }

    public void setAppLocale(String localeCode){
        Configuration config;
        Resources resources = getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        config = resources.getConfiguration();
        config.setLocale(new Locale(localeCode.toLowerCase()));
        resources.updateConfiguration(config, dm);
    }
}