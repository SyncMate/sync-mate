package com.example.cse.syncmate;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class FileListActivity extends AppCompatActivity {

    private String fileName;
    private static final String SYNCMATE_FOLDER_PATH = "/storage/emulated/0/Download/SyncMate/";
    private static final int REQUEST_CODE_COPY_FILES = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_list);

        Intent intent = this.getIntent();
        if (intent != null) {
            fileName = intent.getStringExtra("fileName");
            Log.i("fileName", fileName);
        }

        List<String> folderNameList = viewFiles(fileName);

        if (folderNameList.size() != 0) {
            ListView folderListView = findViewById(R.id.file_list);

            FileAdapter listViewAdapter = new FileAdapter(this, folderNameList, R.drawable.baseline_folder_24);
            folderListView.setAdapter(listViewAdapter);
        } else {
            setContentView(R.layout.empty_folder);
            Button addFiles = findViewById(R.id.add_files_button);
            addFiles.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openFileChooser();
                }
            });
        }
    }

    private List<String> viewFiles(String fileName) {
        File directory = new File(SYNCMATE_FOLDER_PATH, fileName);
        File[] files = directory.listFiles();
        List<String> folderList = new ArrayList<>();
        for (int i = 0; i < files.length; i++) {
            Log.d("Files", "FileName:" + files[i].getName());
            folderList.add(files[i].getName());
        }
        return folderList;
    }

    private void openFileChooser(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(intent, REQUEST_CODE_COPY_FILES);
    }

    @SuppressLint("Range")
    private String getFileName(Uri uri) {
        String result = null;
        if ("content".equals(uri.getScheme())) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    private void copyFileToFolder(Uri sourceUri, File destFolder, String fileName) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(sourceUri);
            File destFile = new File(destFolder, fileName);
            OutputStream outputStream = Files.newOutputStream(destFile.toPath());
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
            inputStream.close();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            return;
        }
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_COPY_FILES) {
            File createdFolder = new File (SYNCMATE_FOLDER_PATH, fileName);
            if (data.getClipData() != null) {
                for (int i = 0; i < data.getClipData().getItemCount(); i++) {
                    Uri uri = data.getClipData().getItemAt(i).getUri();
                    String fileName = getFileName(uri);
                    copyFileToFolder(uri, createdFolder, fileName);
                }
            } else {
                Uri uri = data.getData();
                String fileName = getFileName(uri);
                copyFileToFolder(uri, createdFolder, fileName);
            }
        }
    }

}