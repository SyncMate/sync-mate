package com.example.cse.syncmate.Send;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Random;

public class FileSender {

    private static final int BUFFER_SIZE = 1024;
    private static final int TIMEOUT = 5000; // Timeout in milliseconds
    private static int receiver = 1024;
    private static boolean isTransferPaused = false; // Flag to indicate if the transfer is paused
    private static volatile boolean isTransferCancelled = false;

    public static int receiverPort = 1024;

    public interface FileTransferCallback {
        void onSuccess();

        void onFailure(String errorMessage);

        void onTransferStarted();

        void onTransferCompleted();

        void onTransferFailed(String errorMessage);

        void onTransferPaused();

        void onTransferResumed();

        void onTransferCancelled();
    }

    public static void sendFile(String ipAddress, File file, FileTransferCallback callback) {

        int finalReceiverPort = findAvailablePort(1024, 65535);
        new Thread(() -> {
            try {
                // Get the IP address of the target device
                InetAddress address = InetAddress.getByName(ipAddress);

                // Create a socket connection
                String address_str = String.valueOf(address);
                address_str = address_str.replace("/", "");

                Log.d("File to receive ip", address_str);
                Log.d("File to receive port", String.valueOf(finalReceiverPort));

                Socket socket = new Socket(address_str, receiverPort);
                Log.d("File sending socket", String.valueOf(socket.getLocalPort()));

                // Retrieve the dynamically assigned port
                receiver = socket.getLocalPort();
                Log.d("File RECEIVER_PORT", String.valueOf(socket.getLocalPort()));
                // Create input and output streams for file transfer
                BufferedInputStream bis = new BufferedInputStream(Files.newInputStream(file.toPath()));
                BufferedOutputStream bos = new BufferedOutputStream(socket.getOutputStream());

                String parentFolder = getFolderNameFromPath(file.toString());
                bos.write(parentFolder.length());
                // Send the parent folder name as bytes
                bos.write(parentFolder.getBytes());

                String fileName = file.getName();
                // Send the file name length as a single byte
                bos.write(fileName.length());
                // Send the file name as bytes
                bos.write(fileName.getBytes());

                // Read the file and send it in chunks
                byte[] buffer = new byte[BUFFER_SIZE];
                int bytesRead;
                while ((bytesRead = bis.read(buffer)) != -1) {
                    if (isTransferCancelled) {
                        Log.d("Cancel btn", "CAME INSIDE");
                        break; // Transfer is cancelled, exit the loop
                    }
                    bos.write(buffer, 0, bytesRead);
                }
//                        if (callback != null) {
//                            callback.onTransferCancelled();
//                        }
                // Flush and close the output stream
                bos.flush();
                bos.close();

                // Close the socket connection
                socket.close();

                // Notify the caller about the successful file transfer
                callback.onSuccess();
            } catch (IOException e) {
                e.printStackTrace();

                // Notify the caller about the file transfer failure
                callback.onFailure(e.getMessage());
            }

        }).start();

    }

    // Method to pause the file transfer
    public static void pauseTransfer() {
        isTransferPaused = true;
    }

    // Method to resume the file transfer
    public static void resumeTransfer() {
        isTransferPaused = false;
    }

    // Method to cancel the file transfer
    public static void cancelTransfer() {
        isTransferCancelled = true;

        Log.d("Cancel in FileSender", String.valueOf(isTransferCancelled));
    }

    private static int findAvailablePort(int minPortNumber, int maxPortNumber) {
        Random random = new Random();
        int portRange = maxPortNumber - minPortNumber + 1;

        for (int i = 0; i < portRange; i++) {
            int port = random.nextInt(portRange) + minPortNumber;
            try (ServerSocket serverSocket = new ServerSocket(port)) {
                // If the port is available, return it
                return port;
            } catch (IOException ignored) {
                // Port is not available, try the next one
            }
        }

        // No available port found in the range
        return -1;
    }

    private static String getFolderNameFromPath(String filePath) {
        File file = new File(filePath);
        String parentPath = file.getParent();
        if (parentPath != null) {
            File parentFile = new File(parentPath);
            return parentFile.getName();
        } else {
            return null;
        }
    }

}

// TODO - SEND AND RECEIVE LOCATION - INTERNAL STORAGE/SYNCMATE/<SELECTED FOLDER>
// TODO - SEND RECEIVER'S LISTENING PORT TO FILE SENDER
