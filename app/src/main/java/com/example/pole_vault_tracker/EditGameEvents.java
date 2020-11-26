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
import com.example.pole_vault_tracker.storage.Remote;

import java.sql.SQLException;

public class EditGameEvents extends AppCompatActivity {
    private final EditGameEvents thisPointer = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_game_events);
        findViewById(R.id.new_jump_btn).setOnClickListener(v -> {
            long jumpID = Local.insertJump(this, Local.getActiveGameID(this));
            new Thread(() -> {
                try {
                    Remote.insertJump(jumpID, Local.getActiveGameID(this));
                } catch (SQLException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }).start();
            startEditJumpActivity(jumpID);
        });

        RecyclerView recyclerView = findViewById(R.id.previous_jumps_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        JumpAdapter jumpAdapter = new JumpAdapter();
        jumpAdapter.submitList(Local.getJumps(this, Local.getActiveGameID(this)));
        jumpAdapter.setOnItemClickListener((integer, view) -> startEditJumpActivity(jumpAdapter.getCurrentList().get(integer).getId()));
        recyclerView.setAdapter(jumpAdapter);
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                long jumpID = jumpAdapter.getCurrentList().get(viewHolder.getAdapterPosition()).getId();
                Local.deleteJump(thisPointer, jumpID);
                new Thread(() -> {
                    try {
                        Remote.deleteJump(jumpID);
                    } catch (SQLException | ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }).start();
                jumpAdapter.submitList(Local.getJumps(thisPointer, Local.getActiveGameID(thisPointer)));
            }
        }).attachToRecyclerView(recyclerView);
    }

    private void startEditJumpActivity(long jumpID) {
        Local.setActiveJumpID(this, jumpID);
        startActivity(new Intent(this, EditJump.class));
    }
}