package com.example.cse.syncmate.Send.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
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
            WifiDeviceScanner wifiDeviceScanner = new WifiDeviceScanner(getContext());
            Log.d("WifiDeviceScanner", "PASSED INITIALIZATION");

            List<List<String>> eligibleDevices = wifiDeviceScanner.scanForDevices();
            if (eligibleDevices.isEmpty()) {
                Log.d("WifiDeviceScanner ELIGIBILE LIST SIZE", String.valueOf(eligibleDevices.size()));
                Toast.makeText(getContext(), "NO DEVICES FOUND", Toast.LENGTH_SHORT).show();

            } else {
                Log.d("WifiDeviceScanner NO ELIGIBILE LIST", String.valueOf(eligibleDevices.size()) + eligibleDevices);
                Toast.makeText(getContext(), "Eligible devices: " + eligibleDevices, Toast.LENGTH_SHORT).show();

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
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
                    try {
                        Log.d("WifiDeviceScanner", "BEFORE FILE PATH");
                        File fileToSend = new File("/storage/emulated/0/Download/SyncMate/ggg/fff.pdf"); // Replace with the actual file path
                        Log.d("WifiDeviceScanner", "BEFORE SENDING FILE");
                        FileSender.FileTransferCallback callback = new FileSender.FileTransferCallback() {
                            @Override
                            public void onSuccess() {
                                // File transfer successful
                                // Code here to handle the successful case
                                System.out.println("File transfer completed successfully.");
                            }

                            @Override
                            public void onFailure(String errorMessage) {
                                // File transfer failed
                                // Add your code here to handle the failure case
                                System.out.println("File transfer failed. Error: " + errorMessage);
                            }
                        };

                        FileSender.sendFile(selectedDeviceIP, fileToSend, callback);
                        Log.d("WifiDeviceScanner", "AFTER SENDING FILE");
                    } catch (Exception e) {
                        Log.d("WifiDeviceScanner", String.valueOf(e));
                        Log.d("WifiDeviceScanner", "Error in sending file");
                    }
                });
                builder.create().show();
            }
        });

        return view;
    }
}
