package com.example.cse.syncmate.Send.Adapter;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.cse.syncmate.R;
import com.example.cse.syncmate.Send.FileListActivity;
import com.example.cse.syncmate.Send.FileSender;
import com.example.cse.syncmate.Send.WifiDeviceScanner;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class FolderAdapter extends ArrayAdapter<String> {

    private int iconResource1;
    private int iconResource2;

    public FolderAdapter(Context context, List<String> items, int iconResource1, int iconResource2) {
        super(context, 0, items);
        this.iconResource1 = iconResource1;
        this.iconResource2 = iconResource2;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View view, @NonNull ViewGroup parent) {

        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }
        ImageView folderImage = view.findViewById(R.id.folderImage);
        folderImage.setImageResource(iconResource1);

        TextView folderName = view.findViewById(R.id.folderName);
        folderName.setText(getItem(position));

        folderName.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), FileListActivity.class);
            intent.putExtra("fileName", getItem(position));
            getContext().startActivity(intent);
        });

        ImageButton syncImage = view.findViewById(R.id.sync_btn);
        syncImage.setImageResource(iconResource2);

        syncImage.setOnClickListener(view1 -> {
            Log.d("WifiDeviceScanner", "STARTED CLICK METHOD");

            ProgressDialog progressDialog = new ProgressDialog(getContext(), R.style.CustomProgressDialogStyle);
            progressDialog.setMessage("Searching for devices...");
            progressDialog.setCancelable(false);
            progressDialog.show();

            WifiDeviceScanner wifiDeviceScanner = new WifiDeviceScanner(getContext());

            Log.d("WifiDeviceScanner", "PASSED INITIALIZATION");

            Log.d("WifiDeviceScanner", "STARTED CLICK METHOD");

            Handler handler = new Handler(Looper.getMainLooper());

            new Thread(() -> {
                try{
                    List<List<String>> eligibleDevices = wifiDeviceScanner.scanForDevices();
                    if (eligibleDevices.isEmpty()) {
                        handler.post(() -> {
                            Log.d("WifiDeviceScanner ELIGIBILE LIST SIZE", String.valueOf(eligibleDevices.size()));
                            Toast.makeText(getContext(), "NO DEVICES FOUND", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        });

                    } else {
                        handler.post(()->{
                            Log.d("WifiDeviceScanner NO ELIGIBILE LIST", String.valueOf(eligibleDevices.size()) + eligibleDevices);
                            Toast.makeText(getContext(), "Eligible devices: " + eligibleDevices, Toast.LENGTH_SHORT).show();

                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.CustomAlertDialogStyle);
                            builder.setTitle("Select a device to sync with");
                            List<String> deviceNames = new ArrayList<>();
                            List<String> deviceIPs = new ArrayList<>();
                            for (List<String> deviceInfo : eligibleDevices) {
                                deviceNames.add(deviceInfo.get(0));
                            }
                            for (List<String> deviceIP : eligibleDevices) {
                                deviceIPs.add(deviceIP.get(1));
                            }
                            Log.d("WifiDeviceScanner Device IP", deviceIPs.toString());
                            builder.setItems(deviceNames.toArray(new String[0]), (dialog, which) -> {
                                String selectedDeviceName = deviceNames.get(which);
                                String selectedDeviceIP = deviceIPs.get(which);
                                Toast.makeText(getContext(), "Selected device: " + selectedDeviceName, Toast.LENGTH_SHORT).show();
                                Toast.makeText(getContext(), "Selected device IP: " + selectedDeviceIP, Toast.LENGTH_SHORT).show();

                                new Thread(() -> {
                                    try {
                                        Log.d("WifiDeviceScanner", "BEFORE FILE PATH");

                                        // Get the directory where your app can store files
                                        String path = "/storage/emulated/0/Download/SyncMate/";

                                        // get path of selected folder
                                        String selectedFolder = getItem(position);
                                        Path basePath = Paths.get(path);
                                        Path fullPath = basePath.resolve(selectedFolder);
                                        String finalPath = fullPath.toString();

                                        // get files in the selected folder
                                        File directory = new File(finalPath);
                                        File[] files = directory.listFiles();

                                        Log.d("FileSelected", finalPath);
                                        for (File fileName : files) {
                                            File fileToSend = new File(finalPath, fileName.getName());
                                            Log.d("FileSelected", String.valueOf(fileToSend));
                                            Log.d("WifiDeviceScanner", "BEFORE SENDING FILE");
                                            FileSender.FileTransferCallback callback = new FileSender.FileTransferCallback() {
                                                @Override
                                                public void onSuccess() {
                                                    // File transfer successful
                                                    System.out.println("File transfer completed successfully.");
                                                }

                                                @Override
                                                public void onFailure(String errorMessage) {
                                                    // File transfer failed
                                                    System.out.println("File transfer failed. Error: " + errorMessage);
                                                }

                                                @Override
                                                public void onTransferStarted() {

                                                }

                                                @Override
                                                public void onTransferCompleted() {

                                                }

                                                @Override
                                                public void onTransferFailed(String errorMessage) {

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
                                            };

                                            // Call the handleFileTransfer method and pass the file path
                                            FileSender.sendFile(selectedDeviceIP,fileToSend,callback);
                                            //fileSenderActivity.handleFileTransfer(Uri.fromFile(fileToSend), selectedDeviceIP);
                                            Log.d("WifiDeviceScanner", "AFTER SENDING FILE");
                                        }
                                    } catch (Exception e) {
                                        Log.d("WifiDeviceScanner", String.valueOf(e));
                                        Log.d("WifiDeviceScanner", "Error in sending file");
                                    }
                                    handler.post(() -> progressDialog.dismiss());
                                }).start();

                            });
                            progressDialog.dismiss();
                            builder.create().show();

                        });
                    }
                } catch (Exception e) {
                    Log.d("WifiDeviceScanner", String.valueOf(e));
                    Log.d("WifiDeviceScanner", "Error in initializing WifiDeviceScanner");
                    progressDialog.dismiss();
                }
            }).start();
        });

        return view;
    }
}
