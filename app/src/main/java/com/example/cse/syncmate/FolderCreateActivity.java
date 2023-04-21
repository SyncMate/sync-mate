package com.example.cse.syncmate;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.File;

public class FolderCreateActivity extends AppCompatActivity {

    Button createFolder;
    EditText editFolderName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folder_create);

        createFolder = findViewById(R.id.folder_create_button);
        editFolderName = findViewById(R.id.folder_name);

        // Create a folder in SyncMate folder
        createFolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String folderName = editFolderName.getText().toString();
                createFolder(folderName);
            }
        });
    }

    private void createFolder(String folderName) {
        String directoryPath = "/storage/emulated/0/Download/SyncMate/";
        File folder = new File(directoryPath, folderName);
        if (!folder.exists()) {
            boolean success = folder.mkdirs();
            if (success) {
                Log.d("TAG", "Folder created successfully");
            } else {
                Log.e("TAG", "Failed to create folder");
            }
        }
    }
}