package com.example.cse.syncmate.Send;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;

public class FileSenderActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_PERMISSIONS = 1;
    private static final String TAG = "FileSenderActivity";

    private Uri fileUri;

//    private final ActivityResultLauncher<Intent> filePickerLauncher = registerForActivityResult(
//            new ActivityResultContracts.StartActivityForResult(),
//            new ActivityResultCallback<ActivityResult>() {
//                @Override
//                public void onActivityResult(ActivityResult result) {
//                    if (result.getResultCode() == RESULT_OK) {
//                        Intent data = result.getData();
//                        if (data != null) {
//                            fileUri = data.getData();
//                            Log.d(TAG, "Selected File URI: " + fileUri.toString());
//
//                            // Continue with file transfer or other operations
//                            handleFileTransfer(fileUri);
//                        } else {
//                            // File selection canceled by the user
//                            Log.d(TAG, "File selection canceled");
//                        }
//                    }
//                }
//            });
    private String selectedDeviceIP;
    private final ActivityResultLauncher<Intent> filePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            Uri fileUri = data.getData();
                            Log.d(TAG, "Selected File URI: " + fileUri.toString());

                            // Continue with file transfer or other operations
                            handleFileTransfer(fileUri, selectedDeviceIP);
                        } else {
                            // File selection canceled by the user
                            Log.d(TAG, "File selection canceled");
                        }
                    }
                }
            });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_file_sender);

        // Check if the app has the necessary permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {

            // Request the permissions from the user
            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    },
                    REQUEST_CODE_PERMISSIONS);
        } else {
            // Permissions are already granted, proceed with file selection
            selectFile();
        }
    }

    private void selectFile() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("*/*"); // Set the file type you want to select here, e.g., "application/pdf"
        filePickerLauncher.launch(intent);
    }

//    private void handleFileTransfer(Uri fileUri) {
//        // Get the file name from the URI
//        String fileName = getFileName(fileUri);
//
//        if (fileName != null) {
//            File sourceFile = new File(fileUri.getPath());
//            File destinationFile = new File(getExternalFilesDir(null), fileName);
//
//            try {
//                // Copy the selected file to the destination directory
//                Files.copy(sourceFile.toPath(), destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
//                Log.d(TAG, "File transferred successfully: " + destinationFile.getAbsolutePath());
//
//                // Implement the file transfer logic to send the file to the receiver
//                sendFileToReceiver(destinationFile);
//
//            } catch (IOException e) {
//                e.printStackTrace();
//                Toast.makeText(this, "Failed to transfer file.", Toast.LENGTH_SHORT).show();
//            }
//        } else {
//            Log.d(TAG, "Failed to get file name from URI.");
//            Toast.makeText(this, "Failed to get file name.", Toast.LENGTH_SHORT).show();
//        }
//    }


    public void handleFileTransfer(Uri fileUri, String selectedDeviceIP) {
        String filePath = getPathFromUri(fileUri);

        if (filePath != null) {
            File fileToSend = new File(filePath);
            if (fileToSend.exists()) {
                // Call the sendFile method of FileSender class
                FileSender.sendFile(selectedDeviceIP, fileToSend, new FileSender.FileTransferCallback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onFailure(String errorMessage) {

                    }

                    @Override
                    public void onTransferStarted() {
                        // File transfer started
                        Log.d(TAG, "File transfer started");
                    }

                    @Override
                    public void onTransferCompleted() {
                        // File transfer completed successfully
                        Log.d(TAG, "File transfer completed");
                    }

                    @Override
                    public void onTransferFailed(String errorMessage) {
                        // File transfer failed
                        Log.d(TAG, "File transfer failed. Error: " + errorMessage);
                    }

                    @Override
                    public void onTransferPaused() {

                    }

                    @Override
                    public void onTransferResumed() {

                    }

                    @Override
                    public void onTransferCancelled() {

                    }
                });
            } else {
                // File does not exist
                Log.d(TAG, "File does not exist: " + filePath);
            }
        } else {
            // Invalid file URI
            Log.d(TAG, "Invalid file URI: " + fileUri);
        }
    }

    private String getPathFromUri(Uri uri) {
        String filePath = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if (index != -1) {
                    String displayName = cursor.getString(index);
                    if (displayName != null) {
                        filePath = getCacheDir().getAbsolutePath() + File.separator + displayName;
                    }
                }
                cursor.close();
            }
        } else if (uri.getScheme().equals("file")) {
            filePath = uri.getPath();
        }
        return filePath;
    }
    private void sendFileToReceiver(File file) {
        // Implement the code to send the file to the receiver
        // You can use your existing implementation here
    }

    private String getFileName(Uri uri) {
        String fileName = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    fileName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)+1);
                }
            }
        } else if (uri.getScheme().equals("file")) {
            fileName = new File(uri.getPath()).getName();
        }
        return fileName;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permissions granted, proceed with file selection
                selectFile();
            } else {
                // Permissions denied, handle the error or show a message to the user
                Toast.makeText(this, "Permission denied. Cannot access storage.", Toast.LENGTH_SHORT).show();
                finish(); // Close the activity if permissions are not granted
            }
        }
    }
}
