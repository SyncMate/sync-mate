package com.example.cse.syncmate.Receive;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.example.cse.syncmate.Send.FileSender;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileReceiver {

    private static final int MIN_PORT = 1023;
    private static final int MAX_PORT = 65535;
    static List<String> receivedFiles = new ArrayList<>();
    static List<String> receivedFilesToPass = new ArrayList<>();
    static String name = "";
    private static Context context;
    private ReceiveFragment receiveFragment;

    public FileReceiver(Context context) {
        this.context = context;
    }

    public static void main(String[] args) {

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

            // Accept incoming connections indefinitely
            while (true) {
                Log.d("FileReceiver WHILE LOOP", "CAME INSIDE WHILE LOOP");

                // Accept the connection from the sender
                Socket socket = serverSocket.accept();
                Log.d("FileReceiver ACCEPT SERVERSOCKET", String.valueOf(serverSocket.isClosed()));

                handleFileTransfer(socket);
            }
        } catch (IOException e) {
            Log.d("FileReceiver ERROR", String.valueOf(e));
            e.printStackTrace();
        }
    }

    private static void handleFileTransfer(Socket socket) throws IOException {
        BufferedInputStream bis = new BufferedInputStream(socket.getInputStream());

        // Specify the directory path to save the received files
        String saveDirectory = "/storage/emulated/0/Download/SyncMate/"; //TODO - Replace with the desired file path
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
                String saveFilePath = parentFolder.getAbsolutePath() + File.separator + selectedFileName;

                File existingFile = new File(saveFilePath);
                if (!existingFile.exists()) {
                    FileOutputStream fos = new FileOutputStream(saveFilePath);
                    Log.d("FileReceiver FOS", fos.toString());


                    // Read the file data from the socket and save it
                    byte[] buffer = new byte[1024];
                    while ((bytesRead = bis.read(buffer)) != -1) {
                        fos.write(buffer, 0, bytesRead);
                    }

                    fos.close();

                    Log.d("Received file name", selectedFileName);
                    receivedFiles.add(selectedFileName);
                    // Call the receiveFile() method to update the UI with the received file name

                    new Handler(Looper.getMainLooper()).post(() -> receiveFile(selectedFileName));
                    // Read a single byte as a signal
                    int signal = bis.read();
                    if (signal == -1 || signal != 1) {
                        // Exit the loop if the signal indicates no more files
                        moreFiles = false;
                    }
                }
            }

            // Send files in receiver's folder to sender
            List<String> receiverParentFolder1 = Arrays.asList(parentFolder.list());

            boolean isContain = receivedFiles.containsAll(receiverParentFolder1);

            InetAddress senderAddress = socket.getInetAddress();
            String senderIP = senderAddress.getHostAddress();
//            Log.d("Received file sender", senderIP);

//            String senderDeviceName = senderAddress.getHostName();
//            Log.d("Received file sender", senderDeviceName);
//            Toast.makeText(context, senderDeviceName + "is sending files", Toast.LENGTH_SHORT).show();
//            new Handler(Looper.getMainLooper()).post(() -> getName(senderDeviceName));

            Log.d("CHECK RECEIVER FOLDER", String.valueOf(isContain));
            while (isContain == false) {
                for (String file : receiverParentFolder1) {
                    String saveReceivingFilePath1 = parentFolder.getAbsolutePath() + File.separator + file;
                    File fileToSend = new File(saveReceivingFilePath1);

                    if (!receivedFiles.contains(fileToSend.getName())) {
                        FileSender.sendFile(senderIP, fileToSend, new FileSender.FileTransferCallback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onFailure(String errorMessage) {

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
                        });
                        receivedFiles.add(fileToSend.getName());
                    }
                }
                isContain = true;
            }
        } catch (Exception e) {
            Log.d("File Save Error", e.toString());
        }

        socket.close();
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

    private static void receiveFile(String fileName) {
        receivedFilesToPass.add(fileName);

        // Call the method in the ReceiveFragment to update the UI
        ReceiveFragment.updateReceivedFiles(receivedFilesToPass);
    }

    private static void getName(String senderName){
        name = senderName;
        ReceiveFragment.senderDeviceNameReceived(name);
    }
}
