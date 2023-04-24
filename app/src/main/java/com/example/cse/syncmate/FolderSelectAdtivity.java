package com.example.cse.syncmate;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class FolderSelectAdtivity extends AppCompatActivity {

    Button fileSelectBtn;
    TextView txt;
    int requestCode = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folder_select_adtivity);

        fileSelectBtn = findViewById(R.id.file_select_btn);
        txt = findViewById(R.id.selectedFiles);

        fileSelectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileChooser();
            }
        });

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
            StringBuilder sb = new StringBuilder("Selected files:\n");

            for (int i = 0; i < data.getClipData().getItemCount(); i++) {
                Uri uri = data.getClipData().getItemAt(i).getUri();
                String fileName = getFileName(uri);
                sb.append(fileName).append("\n");
                copyFileToFolder(uri, syncmate, fileName);
            }
            txt.setText(sb.toString());
        } else {

            Uri uri = data.getData();
            String fileName = getFileName(uri);
            copyFileToFolder(uri, syncmate, fileName);
            txt.setText("Selected files:\n" + fileName);
        }
    }

    public void openFileChooser(){
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