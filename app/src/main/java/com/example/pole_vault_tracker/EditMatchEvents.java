package com.example.pole_vault_tracker;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class EditMatchEvents extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_match_events);
        findViewById(R.id.new_jump_btn).setOnClickListener(v -> startActivity(new Intent(this, EditJump.class)));
    }
}