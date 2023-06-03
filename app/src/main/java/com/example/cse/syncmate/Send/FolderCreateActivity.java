package com.example.cse.syncmate.Send;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.documentfile.provider.DocumentFile;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cse.syncmate.R;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;

public class FolderCreateActivity extends AppCompatActivity {
    Button createFolder, selectFolder;
    EditText editFolderName;
    TextView addFileText, cancelText;
    String createdFolderName;
    private static final String SYNCMATE_FOLDER_PATH = "/storage/emulated/0/Download/SyncMate/";
    private static final int REQUEST_CODE_COPY_FILES = 1;
    private static final int REQUEST_CODE_COPY_FOLDER = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folder_create);

        createFolder = findViewById(R.id.folder_create_button);
        editFolderName = findViewById(R.id.folder_name);
        selectFolder = findViewById(R.id.select_folder_button);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.custom_action_bar);
        getSupportActionBar().setElevation(0);

        // Create a folder in SyncMate folder
        createFolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String folderName = editFolderName.getText().toString();
                createdFolderName = folderName;
                if (editFolderName.length() != 0) {
                    createFolder(folderName);
                    openDialogBox();
                } else {
                    Toast.makeText(getApplicationContext(), "Please enter a folder name", Toast.LENGTH_SHORT).show();
                }

            }
        });

        // Select an existing folder
        selectFolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectFolders();
            }
        });
    }

    private void createFolder(String folderName) {
        File folder = new File(SYNCMATE_FOLDER_PATH, folderName);
        if (!folder.exists()) {
            boolean success = folder.mkdirs();
            if (success) {
                Log.d("TAG", "Folder created successfully");
            } else {
                Log.e("TAG", "Failed to create folder");
            }
        }
    }
    private void openDialogBox() {
        Dialog dialog = new Dialog(FolderCreateActivity.this);
        dialog.setContentView(R.layout.dialog_layout);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        addFileText = dialog.findViewById(R.id.add_file_text);
        cancelText = dialog.findViewById(R.id.cancel_text);
        addFileText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                openFileChooser();
            }
        });
        cancelText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                finish();
            }
        });
        dialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            return;
        }
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_COPY_FILES) {
            File createdFolder = new File (SYNCMATE_FOLDER_PATH, createdFolderName);
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
            // Redirect to previous page
            onBackPressed();
        }
        else if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_COPY_FOLDER) {
            Uri uri = data.getData();
            DocumentFile pickedDir = DocumentFile.fromTreeUri(this, uri);
            if (pickedDir != null) {
                copyDirectory(pickedDir);
            }
            // Redirect to previous page
            onBackPressed();
        }
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

    private void selectFolders() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        startActivityForResult(intent, REQUEST_CODE_COPY_FOLDER);
    }

    private void copyDirectory(DocumentFile sourceDirectory) {
        String displayName = sourceDirectory.getName();
        File destinationFolder = new File(SYNCMATE_FOLDER_PATH,displayName);

        if (!destinationFolder.exists()) {
            destinationFolder.mkdirs();
        }
        for (DocumentFile sourceFile : sourceDirectory.listFiles()) {
            String fileName = sourceFile.getName();
            Uri sourceUri = sourceFile.getUri();
            copyFileToFolder(sourceUri,destinationFolder, fileName);
        }
    }
}