package com.example.cse.syncmate.Receive;

import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
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
//                DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
//
//                //Read the number of files being sent
//                int numFiles = dataInputStream.readInt();
//                Log.d("numberInput", String.valueOf(dataInputStream));
//                for (int i = 0; i < numFiles; i++) {
//                    //Read the file name
//                    String fileName = dataInputStream.readUTF();
//                    handleFileTransfer(socket, fileName);
//                }
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
//        String saveFilePath = "/storage/emulated/0/Download/SyncMate/Hooks.pdf"; //TODO - Replace with the desired file path
        // Specify the directory path to save the received files
        String saveDirectory = "/storage/emulated/0/Download/SyncMate/";
        File directory = new File(saveDirectory);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        try {
            // Read the parent folder name length
            int folderNameLength = bis.read();
            if (folderNameLength == -1) {
                throw new IOException("Invalid protocol");
            }

            // Read the parent folder name
            byte[] folderNameBytes = new byte[folderNameLength];
            int bytesRead = bis.read(folderNameBytes);
            if (bytesRead == -1) {
                throw new IOException("Invalid protocol");
            }
            String folderName = new String(folderNameBytes);

            // Create a folder with the parent folder name
            File parentFolder = new File(saveDirectory, folderName);
            if (!parentFolder.exists()) {
                parentFolder.mkdirs();
            }

            boolean moreFiles = true;
            while (moreFiles) {

                int fileNameLength = bis.read();
                if (fileNameLength == -1) {
                    break; // Exit the loop if no more files are expected
                }

                // Read the file name
                byte[] fileNameBytes = new byte[fileNameLength];
                bytesRead = bis.read(fileNameBytes);
                if (bytesRead == -1) {
                    break; // Exit the loop if no more files are expected
                }

                String selectedFileName = new String(fileNameBytes);

                // Specify the file path to save the received file
                // String saveFilePath = saveDirectory + "File" + fileCount + "_" + System.currentTimeMillis() + ".pdf";
                String saveFilePath = parentFolder.getAbsolutePath() + File.separator + selectedFileName;

                FileOutputStream fos = new FileOutputStream(saveFilePath);
                Log.d("FileReceiver FOS", fos.toString());


                // Read the file data from the socket and save it
                byte[] buffer = new byte[1024];
//                int bytesRead;
                while ((bytesRead = bis.read(buffer)) != -1) {
                    fos.write(buffer, 0, bytesRead);
                }

                // Close the output stream
                fos.close();

                // Check if there are more files to receive
                int signal = bis.read(); // Read a single byte as a signal
                if (signal == -1 || signal != 1) {
                    moreFiles = false; // Exit the loop if the signal indicates no more files
                }
                Log.d("recfilename", String.valueOf(moreFiles));
            }

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
