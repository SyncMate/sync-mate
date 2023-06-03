package com.example.cse.syncmate.Send;

import android.content.Context;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Random;

public class FileSender {

    private static int RECEIVER_PORT = 1024; // Will be dynamically assigned

    private static final int BUFFER_SIZE = 1024;
    private static final int TIMEOUT = 5000; // Timeout in milliseconds

    private static volatile boolean isPaused = false;
    private static volatile boolean isCancelled = false;
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

//                int listeningPort = FileReceiver.startFileReceiver(address_str);
//                int listeningPort = FileReceiver.getListeningPort()[0];
//                int[] result = FileReceiver.getListeningPort();
//                Log.d("File sending listening port", String.valueOf(result[0]));
//                Log.d("File sending listening ip address", String.valueOf(result[1]));

                //TODO - Get receiver port here
                Socket socket = new Socket(address_str, receiverPort);
                Log.d("File sending socket", String.valueOf(socket.getLocalPort()));

                // Retrieve the dynamically assigned port
                RECEIVER_PORT = socket.getLocalPort();
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

                    // Check if the transfer is paused or cancelled
                    if (isPaused) {
                        callback.onTransferPaused();
                        while (isPaused) {
                            // Wait until resumed or cancelled
                            if (isCancelled) {
                                // Clean up and notify the caller about the transfer cancellation
                                bis.close();
                                bos.close();
                                socket.close();
                                callback.onTransferCancelled();
                                return;
                            }
                            Thread.sleep(TIMEOUT);
                        }
                        callback.onTransferResumed();
                    } else if (isCancelled) {
                        // Clean up and notify the caller about the transfer cancellation
                        bis.close();
                        bos.close();
                        socket.close();
                        callback.onTransferCancelled();
                        return;
                    }

                    bos.write(buffer, 0, bytesRead);
                }

                // Flush and close the output stream
                bos.flush();
                bos.close();

                // Close the socket connection
                socket.close();

                // Notify the caller about the successful file transfer
                callback.onSuccess();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();

                // Notify the caller about the file transfer failure
                callback.onFailure(e.getMessage());
            }

        }).start();
        // Start listening for heartbeat messages from the receiver
        startHeartbeatListener(finalReceiverPort);
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

    private static void startHeartbeatListener(int receiverPort) {
        new Thread(() -> {
            try {
                Log.d("File heartbeat: ", "ENTERED START HEARTBEAT LISTENER");
                ServerSocket serverSocket = new ServerSocket(receiverPort);
                Log.d("File to receive port", serverSocket.toString());
                System.out.println("Heartbeat listener is running on port " + receiverPort);

                // Accept incoming connections indefinitely
                while (true) {
                    // Accept the connection from the receiver
                    Socket socket = serverSocket.accept();

                    // Handle the heartbeat message
                    handleHeartbeatMessage(socket);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private static void handleHeartbeatMessage(Socket socket) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        // Read the heartbeat message
        String heartbeatMessage = reader.readLine();
        System.out.println("Received heartbeat: " + heartbeatMessage);

        // Close the socket connection
        socket.close();
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
// TODO - SEND WHOLE CONTENT IN THE FOLDER TO RECEIVER FOLDER
// TODO - SEND RECEIVER'S LISTENING PORT TO FILE SENDER
// TODO - HANDLE THE CASE WHERE MULTIPLE RECEIVERS LISTENING ON PORTS
