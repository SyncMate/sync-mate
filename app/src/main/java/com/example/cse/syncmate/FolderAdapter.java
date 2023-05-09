package com.example.cse.syncmate;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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

        // Create a dialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Select a device");
        AlertDialog dialog = builder.create();

        syncImage.setOnClickListener(view1 -> {
            Log.d("WifiDeviceScanner", "STARTED CLICK METHOD");
            WifiDeviceScanner wifiDeviceScanner = new WifiDeviceScanner(getContext());
            Log.d("WifiDeviceScanner", "PASSED INITIALIZATION");

            List<String> eligibleDevices = wifiDeviceScanner.scanForDevices();
            if (eligibleDevices.isEmpty()) {
                Log.d("WifiDeviceScanner ELIGIBILE LIST SIZE", String.valueOf(eligibleDevices.size()));
                Toast.makeText(getContext(), "NO DEVICES FOUND", Toast.LENGTH_SHORT).show();

            } else {
                Log.d("WifiDeviceScanner NO ELIGIBILE LIST", String.valueOf(eligibleDevices.size()) + eligibleDevices);
                Toast.makeText(getContext(), "Eligible devices: " + eligibleDevices, Toast.LENGTH_SHORT).show();

                dialog.show();
                // Set the items in the dialog to the list of devices
                builder.setItems(eligibleDevices.toArray(new String[0]), (dialog1, which) -> {
                    // Code to handle when an item is clicked
                    String selectedDeviceName = eligibleDevices.get(which);
                    Toast.makeText(getContext(), "Selected device: " + selectedDeviceName, Toast.LENGTH_SHORT).show();
                });
            }
        });

        return view;
    }
}
