package com.example.cse.syncmate.Receive;

import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

public class FileReceiver {

    private static final int MIN_PORT = 1023;
    private static final int MAX_PORT = 65535;
    private static final int HEARTBEAT_INTERVAL = 5000; // 5 seconds

    public static void main(String[] args) {

        // StrictMode
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Log.d("File Receive", "CAME INTO MAIN CODE");
        int port = findAvailablePort();
        Log.d("File Receive", "SCANNED AVAILABLE PORTS");
        if (port == -1) {
            System.out.println("No available ports found. Exiting...");
            return;
        }

        try {
            Log.d("FileReceiver", "BEFORE GET SERVER SOCKET");

            // Create a server socket and bind it to the selected port
            ServerSocket serverSocket = new ServerSocket(port);
            serverSocket.setReuseAddress(true);

            Log.d("FileReceiver SERVERSOCKET", serverSocket.toString());

            System.out.println("FileReceiver is listening on port " + port);

            // Start the heartbeat thread
            //startHeartbeatThread(serverSocket, port);
            // Accept incoming connections indefinitely
            while (true) {
                Log.d("FileReceiver WHILE LOOP", "CAME INSIDE WHILE LOOP");

                // Accept the connection from the sender
                Socket socket = serverSocket.accept();
                Log.d("FileReceiver ACCEPT SERVERSOCKET", String.valueOf(serverSocket.isClosed()));

                // Handle the file transfer
                handleFileTransfer(socket);
            }
        } catch (IOException e) {
            Log.d("FileReceiver ERROR", String.valueOf(e));
            e.printStackTrace();
        }
    }

    private static void handleFileTransfer(Socket socket) throws IOException {
        BufferedInputStream bis = new BufferedInputStream(socket.getInputStream());
        // Specify the file path to save the received file
        String saveFilePath = "/storage/emulated/0/Download/SyncMate/Hooks.pdf"; //TODO - Replace with the desired file path

        try {
            FileOutputStream fos = new FileOutputStream(saveFilePath);
            Log.d("FileReceiver FOS", fos.toString());

            // Read the file data from the socket and save it
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = bis.read(buffer)) != -1) {

                Log.d("File read and save", "READ AND SAVE FILE");
                fos.write(buffer, 0, bytesRead);
            }

            // Close the output stream and socket
            fos.close();
        } catch (Exception e) {
            Log.d("File Save Error", e.toString());
        }

        socket.close();

        System.out.println("File received and saved successfully.");
    }

    private static int findAvailablePort() {
        for (int port = MIN_PORT; port <= MAX_PORT; port++) {
            try {
                // Try to bind the server socket to the current port
                ServerSocket serverSocket = new ServerSocket(port);
                serverSocket.close();
                return port;
            } catch (IOException e) {
                // Port is already in use, continue to the next port
            }
        }
        return -1; // No available ports found
    }

//    private static void startHeartbeatThread(ServerSocket serverSocket, int port) {
//        Timer timer = new Timer();
//        timer.scheduleAtFixedRate(new TimerTask() {
//            @Override
//            public void run() {
//                try {
//                    // Send a heartbeat message to the sender
//                    Socket heartbeatSocket = new Socket("192.168.8.197", port);
//                    PrintWriter out = new PrintWriter(heartbeatSocket.getOutputStream(), true);
//                    out.println("Heartbeat");
//                    heartbeatSocket.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }, 0, HEARTBEAT_INTERVAL);
//    }
}
