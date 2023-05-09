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

public class WifiDeviceScanner extends AppCompatActivity {

    private final Context context;

    public WifiDeviceScanner(Context context) {
        this.context = context;
    }

    int PERMISSION_REQUEST_CODE = 1;

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

        Log.d("WifiDeviceScanner", "ENTER TO SCANFORDEVICES METHOD");
        List<List<String>> devices = new ArrayList<>();
        List<String> innerListDevice = new ArrayList<>();
        Log.d("WifiDeviceScanner", "PASSED GET CONTEXT");
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

            if (wifiManager.isWifiEnabled()) {
                Log.d("WifiDeviceScanner", "WIFI ENABLED DEVICE");
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                Log.d("WifiDeviceScanner IP ADDRESS", String.valueOf(wifiInfo.getIpAddress()));
                int ipAddress = wifiInfo.getIpAddress();
                String ipString = String.format("%d.%d.%d.%d", (ipAddress & 0xff), (ipAddress >> 8 & 0xff), (ipAddress >> 16 & 0xff), (ipAddress >> 24 & 0xff));
                Log.d("WifiDeviceScanner IP STRING", ipString);
                String subnet = ipString.substring(0, ipString.lastIndexOf(".") + 1);
                Log.d("WifiDeviceScanner SUBNET", subnet);
                for (int i = 1; i <= 255; i++) {
                    String address = subnet + i;
                    if (address.equals(accessPointIp) || address.equals(ipString)) {
                        continue;
                    }
                    try {
                        Log.d("WifiDeviceScanner i VALUE", String.valueOf(i));
                        InetAddress inetAddress = InetAddress.getByName(address);
                        Log.d("WifiDeviceScanner INETADDRESS", "PASSED INETADDRESS INITIALIZATION");
                        if (inetAddress.isReachable(100)) {
                            Log.d("WifiDeviceScanner", "INSIDE ISREACHABLE");

                            innerListDevice.add(inetAddress.getHostName());
                            innerListDevice.add(address);
                            devices.add(innerListDevice);
                            Log.d("WifiDeviceScanner", "ADDED DEVICE TO LIST");
                        }
                    } catch (UnknownHostException e) {
                        Log.d("WifiDeviceScanner UnknownHostException", address);
                    } catch (IOException ioe) {
                        Log.d("WifiDeviceScanner IOException", ioe.toString());
                        ioe.printStackTrace();
                    }
                }
            }
        } catch (NullPointerException e) {
            Log.e("WifiDeviceScanner ERROR", "Couldn't get Wifi Service");
        }
        Log.d("WifiDeviceScanner TOTAL DEVICES: ", String.valueOf(devices.size()));
        return devices;
    }

}

