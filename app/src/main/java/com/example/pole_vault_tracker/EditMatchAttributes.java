package com.example.pole_vault_tracker;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.Locale;

public class EditMatchAttributes extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {
    private static final int REQUEST_PERMISSION_CODE = 0x486d;
    private TextInputEditText matchLocationEditText;
    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_match_attributes);
        matchLocationEditText = (TextInputEditText) findViewById(R.id.match_location_edit_text);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        ((TextInputLayout) findViewById(R.id.match_location_input_layout)).setEndIconOnClickListener(v -> getLocation());
        ((Button)findViewById(R.id.start_match_btn)).setOnClickListener(v -> startActivity(new Intent(this, EditMatchEvents.class)));
    }

    public void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSION_CODE);
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5, location -> {
            try {
                matchLocationEditText.setText(new Geocoder(this, Locale.getDefault())
                        .getFromLocation(location.getLatitude(), location.getLongitude(), 1).get(0).getAddressLine(0));
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