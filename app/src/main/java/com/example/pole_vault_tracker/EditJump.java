package com.example.pole_vault_tracker;

import android.Manifest;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.example.pole_vault_tracker.storage.Local;
import com.example.pole_vault_tracker.storage.Remote;
import com.example.pole_vault_tracker.storage.model.Jump;
import com.google.android.material.textfield.TextInputLayout;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EditJump extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {
    private static final int REQUEST_IMAGE_CAPTURE = 0xa55;
    private static final int REQUEST_CAMERA_PERMISSION = 0x5f;
    private static final String FILE_PROVIDER = "com.example.android.fileprovider";
    private Jump jump;
    private EditText jumpHeightEditText;
    private EditText descriptionEditText;
    private CheckBox successfulJumpCheckBox;
    private String currentPhotoPath;

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

        ((TextInputLayout) findViewById(R.id.jump_description_input_layout))
                .setEndIconOnClickListener(v -> dispatchTakePictureIntent());
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CAMERA_PERMISSION);
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        FILE_PROVIDER,
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
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

    public void sync() {
        jump.setJumpHeight(Double.parseDouble(jumpHeightEditText.getText().toString()));
        jump.setDescription(descriptionEditText.getText().toString());
        jump.setGameID(Local.getActiveGameID(this));
        jump.setSuccess(successfulJumpCheckBox.isChecked());
        Local.updateJump(this, jump);
        new Thread(() -> {
            try {
                Remote.updateJump(jump);
            } catch (SQLException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }).start();
    }
}