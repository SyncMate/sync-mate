package com.example.cse.syncmate;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SendActivity extends AppCompatActivity {

    Button createFolder;
    ListView folderList;
    List<String> folderNameList;
    ArrayAdapter arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send);

        // Create SyncMate folder in Download directory
        createSyncMateFolder();

        createFolder = findViewById(R.id.folder_create_button);

        createFolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),FolderCreateActivity.class);
                startActivity(i);
            }
        });

        folderNameList = viewFolders();
        folderList = findViewById(R.id.list);
        arrayAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, folderNameList);

        if (folderNameList.size() !=0) {
            folderList.setAdapter(arrayAdapter);

            folderList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Log.d("TAG", "A folder");
                }
            });
        } else {
//            folderList.setEmptyView(findViewById(R.id.empty));
            Log.d("TAG", "No folder");
        }

    }

    private void createSyncMateFolder() {
        String folderName = "SyncMate";
        File folder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), folderName);
        if (!folder.exists()) {
            boolean success = folder.mkdirs();
            if (success) {
                Log.d("TAG", "Folder created successfully");
            } else {
                Log.e("TAG", "Failed to create folder");
            }
        }
    }

    private List<String> viewFolders() {
        String directoryPath = "/storage/emulated/0/Download/SyncMate/";
        File directory = new File(directoryPath);
        List<String> folderList = new ArrayList<>();

         if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        folderList.add(file.getName());
                        Log.d("TAG", "Folder name: " + file.getName());
                    }
                }
            }
        } else {
            Log.e("TAG", "Directory does not exist or is not a directory");
        }
         return folderList;

    }
}