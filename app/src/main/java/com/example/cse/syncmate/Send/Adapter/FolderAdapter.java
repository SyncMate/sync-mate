package com.example.cse.syncmate.Send.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
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
import androidx.appcompat.app.AppCompatActivity;

import com.example.cse.syncmate.Send.FileListActivity;
import com.example.cse.syncmate.R;
import com.example.cse.syncmate.Send.SyncFolderActivity;
import com.example.cse.syncmate.Send.WifiDeviceScanner;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
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
                String ipAddress = eligibleDevices.get(0).get(1);
                Toast.makeText(getContext(), "Eligible devices: " + eligibleDevices, Toast.LENGTH_SHORT).show();

                String path = "/internal storage/MobileTrans/mobiletrans_log/2023-05-07.txt";

//                Intent intent = new Intent(getContext(), SyncFolderActivity.class);
//                intent.putExtra("ipAddress", ipAddress);
//                getContext().startActivity(intent);
            }
        });

        return view;
    }
}
