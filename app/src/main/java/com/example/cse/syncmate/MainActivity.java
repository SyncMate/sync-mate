package com.example.cse.syncmate;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.cse.syncmate.Receive.FileReceiver;
import com.example.cse.syncmate.Receive.ReceiveFragment;
import com.example.cse.syncmate.Send.FolderCreateActivity;
import com.example.cse.syncmate.Send.SendFragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.net.SocketException;

public class MainActivity extends AppCompatActivity
        implements BottomNavigationView
        .OnNavigationItemSelectedListener {

    BottomNavigationView bottomNavigationView;
    FloatingActionButton fab;

    private Uri fileUri;
//    private final ActivityResultLauncher<String> filePickerLauncher = registerForActivityResult(
//            new ActivityResultContracts.OpenDocument(),
//            result -> {
//                if (result != null) {
//                    fileUri = result;
//                    Log.d("TAG", "Selected File URI: " + fileUri.toString());
//
//                    // Continue with file transfer or other operations
//                    // Call the method to handle the file transfer with the selected file URI
//                    handleFileTransfer(fileUri);
//                } else {
//                    // File selection canceled by the user
//                    Log.d("TAG", "File selection canceled");
//                }
//            }
//    );


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navigator);

        bottomNavigationView
                = findViewById(R.id.bottomNavigationView);

        bottomNavigationView
                .setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.send);
        fab = findViewById(R.id.add_folders);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, FolderCreateActivity.class);
                startActivity(intent);
            }
        });

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.custom_action_bar);
        getSupportActionBar().setElevation(0);

        // Call the FileReceiver main method in a separate thread
        new Thread(new Runnable() {
            @Override
            public void run() {
                FileReceiver.main(new String[]{});
            }
        }).start();

//        openFilePicker();

    }

    @Override
    public void onResume() {
        super.onResume();
        if (!Utils.isPermissionGranted(this)) {
            new AlertDialog.Builder(this)
                    .setTitle("All files permission")
                    .setMessage("This app requires all files permission")
                    .setPositiveButton("Allow", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            takePermission();
                        }
                    })
                    .setNegativeButton("Deny", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    }).setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        } else {
//            Toast.makeText(this, "Permission already granted", Toast.LENGTH_SHORT).show();
            Log.d("onResume", "Permission already granted");
        }
    }

    private void takePermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.addCategory("android.intent.category.DEFAULT");
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivityForResult(intent, 101);
            } catch (Exception e) {
                e.printStackTrace();
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                startActivityForResult(intent, 101);
            }
        } else {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, 101);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0) {
            if (requestCode == 101) {
                boolean readExt = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                if (!readExt) takePermission();
            }
        }
    }

    //    private void openFilePicker() {
//        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
//        intent.addCategory(Intent.CATEGORY_OPENABLE);
//        intent.setType("*/*");
//        filePickerLauncher.launch(intent);
//    }

    private void handleFileTransfer(Uri fileUri) {
        // Convert the file URI to a file path if needed
        String filePath = getFilePathFromUri(fileUri);
        Log.d("File Picker", "File Path: " + filePath);

        // Continue with the file transfer process
        // ...
    }

    private String getFilePathFromUri(Uri uri) {
        String filePath = null;
        if (uri != null) {
            if (DocumentsContract.isDocumentUri(this, uri)) {
                // Document URI
                String documentId = DocumentsContract.getDocumentId(uri);
                if (documentId.startsWith("raw:")) {
                    filePath = documentId.substring(4);
                } else {
                    Uri contentUri = ContentUris.withAppendedId(
                            Uri.parse("content://downloads/public_downloads"), Long.parseLong(documentId));
                    filePath = getDataColumn(contentUri, null, null);
                }
            } else if ("content".equalsIgnoreCase(uri.getScheme())) {
                // Content URI
                filePath = getDataColumn(uri, null, null);
            } else if ("file".equalsIgnoreCase(uri.getScheme())) {
                // File URI
                filePath = uri.getPath();
            }
        }
        return filePath;
    }

    private String getDataColumn(Uri uri, String selection, String[] selectionArgs) {
        String column = "_data";
        String[] projection = {column};

        try (Cursor cursor = getContentResolver().query(uri, projection, selection, selectionArgs, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(columnIndex);
            }
        } catch (Exception e) {
            Log.e("File Picker", "Error retrieving file path: " + e.getMessage());
        }
        return null;
    }


    SendFragment send = new SendFragment();
    ReceiveFragment receive = new ReceiveFragment();

    @Override
    public boolean
    onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.send:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.flFragment, send)
                        .commit();
                return true;

            case R.id.receive:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.flFragment, receive)
                        .commit();
                return true;
        }
        return false;
    }
}
