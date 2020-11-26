package com.example.pole_vault_tracker;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.pole_vault_tracker.storage.Local;
import com.example.pole_vault_tracker.storage.model.Jump;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Locale;

public class EditJump extends AppCompatActivity {
    private Jump jump;
    private EditText jumpHeightEditText;
    private EditText descriptionEditText;
    private CheckBox successfulJumpCheckBox;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_jump);
        jumpHeightEditText = findViewById(R.id.jump_height_edit_text);
        descriptionEditText = findViewById(R.id.jump_description_edit_text);
        successfulJumpCheckBox = findViewById(R.id.successful_jump_check_box);
        jump = Local.getJump(this, Local.getActiveJumpID(this));
        jumpHeightEditText.setText(String.valueOf(jump.getJumpHeight()));
        descriptionEditText.setText(jump.getDescription());
        successfulJumpCheckBox.setChecked(jump.isSuccess());
    }

    @Override
    public void finish() {
        sync();
        super.finish();
    }

    @Override
    protected void onStop() {
        sync();
        super.onStop();
    }

    @Override
    protected void onPause() {
        sync();
        super.onPause();
    }

    public void sync(){
        jump.setJumpHeight(Double.parseDouble(jumpHeightEditText.getText().toString()));
        jump.setDescription(descriptionEditText.getText().toString());
        jump.setGameID(Local.getActiveGameID(this));
        jump.setSuccess(successfulJumpCheckBox.isChecked());
        Local.updateJump(this, jump);
    }
}