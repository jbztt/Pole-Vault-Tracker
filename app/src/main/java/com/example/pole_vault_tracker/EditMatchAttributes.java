package com.example.pole_vault_tracker;

import android.Manifest;
import android.content.Context;
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
import com.example.pole_vault_tracker.storage.model.Game;
import com.google.android.material.textfield.TextInputLayout;

import java.io.IOException;
import java.util.Locale;

public class EditMatchAttributes extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {
    private static final int REQUEST_PERMISSION_CODE = 0x486d;
    private EditText matchLocationEditText;
    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_match_attributes);
        matchLocationEditText = findViewById(R.id.match_location_edit_text);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        ((TextInputLayout) findViewById(R.id.match_location_input_layout)).setEndIconOnClickListener(v -> getLocation());

        findViewById(R.id.start_match_btn).setOnClickListener(v -> {
            Local.updateGame(this, new Game(getPreferences(Context.MODE_PRIVATE).getLong(MainActivity.SHARED_PREFERENCE_KEY_GAME_ID, Local.getLastGame(this).getId()), ((EditText)findViewById(R.id.player_name_edit_text)).getText().toString(), matchLocationEditText.getText().toString()));
            startActivity(new Intent(this, EditMatchEvents.class));
        });
    }

    public void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSION_CODE);
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5, location -> {
            try {
                matchLocationEditText.setText(
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
        if (requestCode == REQUEST_PERMISSION_CODE) getLocation();
        else super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}