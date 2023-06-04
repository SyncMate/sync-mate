package com.example.cse.syncmate.Send;

import android.content.Context;
import android.content.pm.PackageManager;
import android.net.DhcpInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class WifiDeviceScanner extends AppCompatActivity {

    private final Context context;

    public WifiDeviceScanner(Context context) {
        this.context = context;
    }

    int PERMISSION_REQUEST_CODE = 1;
    int numThreads = 8;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.INTERNET}, PERMISSION_REQUEST_CODE);
        }
    }

    public List<List<String>> scanForDevices() {

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        List<List<String>> devices = new ArrayList<>();
        int batchSize = 10; // Number of IP addresses to process in each batch
        int numBatches = 255 / batchSize;
        if (255 % batchSize != 0) {
            numBatches++; // Add an extra batch for the remaining IP addresses
        }
        try {
            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

            Log.d("WifiDeviceScanner", "BEFORE WIFI ENABLES");
            DhcpInfo dhcpInfo = wifiManager.getDhcpInfo();
            int gatewayIp = dhcpInfo.gateway;
            String accessPointIp = String.format(
                    "%d.%d.%d.%d",
                    (gatewayIp & 0xff),
                    (gatewayIp >> 8 & 0xff),
                    (gatewayIp >> 16 & 0xff),
                    (gatewayIp >> 24 & 0xff)
            );
            Log.d("WifiDeviceScanner ACCESS POINT IP", accessPointIp);

            // Create a thread pool with 10 threads
            ExecutorService executorService = Executors.newFixedThreadPool(numThreads);

            if (wifiManager.isWifiEnabled()) {
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                Log.d("WifiDeviceScanner IP ADDRESS", String.valueOf(wifiInfo.getIpAddress()));
                int ipAddress = wifiInfo.getIpAddress();
                String ipString = String.format("%d.%d.%d.%d", (ipAddress & 0xff), (ipAddress >> 8 & 0xff), (ipAddress >> 16 & 0xff), (ipAddress >> 24 & 0xff));
                Log.d("WifiDeviceScanner IP STRING", ipString);
                String subnet = ipString.substring(0, ipString.lastIndexOf(".") + 1);
                Log.d("WifiDeviceScanner SUBNET", subnet);

                for (int batch = 0; batch < numBatches; batch++) {
                    int start = batch * batchSize + 1;
                    int end = Math.min(start + batchSize - 1, 255);

                    for (int i = start; i <= end; i++) {
                        String address = subnet + i;
                        if (address.equals(accessPointIp) || address.equals(ipString)) {
                            continue;
                        }
                        int finalI = i;
                        executorService.submit(() -> {
                            try {
                                Log.d("WifiDeviceScanner i VALUE", String.valueOf(finalI));
                                InetAddress inetAddress = InetAddress.getByName(address);
                                if (inetAddress.isReachable(250)) {
                                    List<String> innerListDevice = new ArrayList<>();

                                    innerListDevice.add(inetAddress.getHostName());
                                    innerListDevice.add(address);

                                    synchronized (devices) {
                                        devices.add(innerListDevice);
                                    }
                                    Log.d("WifiDeviceScanner", "ADDED DEVICE TO LIST");
                                }

                            } catch (UnknownHostException e) {
                                Log.d("WifiDeviceScanner UnknownHostException", address);
                            } catch (IOException ioe) {
                                Log.d("WifiDeviceScanner IOException", ioe.toString());
                                ioe.printStackTrace();
                            }
                        });
                    }
                }
            }
            executorService.shutdown();

            try {
                executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
            } catch (InterruptedException e) {
                // Handle exception
            }

            return devices;
        } catch (NullPointerException e) {
            Log.e("WifiDeviceScanner ERROR", "Couldn't get Wifi Service");
        }
        Log.d("WifiDeviceScanner TOTAL DEVICES: ", String.valueOf(devices.size()));
        return devices;
    }

}

