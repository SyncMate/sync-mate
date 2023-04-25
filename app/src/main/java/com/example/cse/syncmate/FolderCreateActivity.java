package com.example.cse.syncmate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FolderCreateActivity extends AppCompatActivity {

    Button createFolder;
    EditText editFolderName;
    TextView add_file_text, cancel_text;

    int requestCode = 1;
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
                if (editFolderName.length() != 0) {
                    createFolder(folderName);
                    openDialogBox();
                } else {
                    Toast.makeText(getApplicationContext(), "Please enter a folder name", Toast.LENGTH_SHORT).show();
                }

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
    private void openDialogBox() {
        Dialog dialog = new Dialog(FolderCreateActivity.this);
        dialog.setContentView(R.layout.dialog_layout);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);

        add_file_text = dialog.findViewById(R.id.add_file_text);
        cancel_text = dialog.findViewById(R.id.cancel_text);
        add_file_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                openFileChooser();
                finish();
            }
        });
        cancel_text.setOnClickListener(new View.OnClickListener() {
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

        String directoryPath = "/storage/emulated/0/Download/SyncMate/";
        File syncmate = new File (directoryPath);
        if (data == null) {
            return;
        }
        if (data.getClipData() != null) {
            for (int i = 0; i < data.getClipData().getItemCount(); i++) {
                Uri uri = data.getClipData().getItemAt(i).getUri();
                String fileName = getFileName(uri);
                copyFileToFolder(uri, syncmate, fileName);
            }
        } else {
            Uri uri = data.getData();
            String fileName = getFileName(uri);
            copyFileToFolder(uri, syncmate, fileName);
        }
    }

    private void openFileChooser(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(intent, requestCode);
    }

    @SuppressLint("Range")
    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
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
            OutputStream outputStream = new FileOutputStream(destFile);
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
}