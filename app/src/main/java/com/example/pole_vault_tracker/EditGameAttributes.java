package com.example.pole_vault_tracker;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.pole_vault_tracker.storage.Local;
import com.example.pole_vault_tracker.storage.Remote;
import com.example.pole_vault_tracker.storage.model.Game;
import com.google.android.material.textfield.TextInputLayout;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Locale;

public class EditGameAttributes extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {
    private static final int REQUEST_LOCATION_SERVICE_PERMISSION_CODE = 0x486d;
    private EditText gameLocationEditText;
    private EditText playerNameEditText;
    private LocationManager locationManager;
    private Game game;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_game_attributes);
        gameLocationEditText = findViewById(R.id.match_location_edit_text);
        playerNameEditText = findViewById(R.id.player_name_edit_text);
        game = Local.getGame(this, Local.getActiveGameID(this));
        playerNameEditText.setText(game.getPlayerName());
        gameLocationEditText.setText(game.getLocation());

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        ((TextInputLayout) findViewById(R.id.match_location_input_layout)).setEndIconOnClickListener(v -> getLocation());

        findViewById(R.id.start_game_btn).setOnClickListener(v -> {
            sync();
            startActivity(new Intent(this, EditGameEvents.class));
        });
    }

    public void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_SERVICE_PERMISSION_CODE);
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5, location -> {
            try {
                gameLocationEditText.setText(
                        new Geocoder(this, Locale.getDefault()).getFromLocation(
                                location.getLatitude(), location.getLongitude(), 1).get(0).getAddressLine(0)
                );
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_LOCATION_SERVICE_PERMISSION_CODE) getLocation();
        else super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void finish() {
        sync();
        super.finish();
    }

    @Override
    protected void onPause() {
        sync();
        super.onPause();
    }

    @Override
    protected void onStop() {
        sync();
        super.onStop();
    }

    private void sync() {
        game.setLocation(gameLocationEditText.getText().toString());
        game.setPlayerName(playerNameEditText.getText().toString());
        new Thread(() -> {
            try {
                Remote.updateGame(game);
            } catch (SQLException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }).start();
        Local.updateGame(this, game);
    }
}