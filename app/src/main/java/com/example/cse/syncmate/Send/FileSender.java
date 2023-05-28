package com.example.cse.syncmate.Send;

import android.os.StrictMode;
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

    private static int RECEIVER_PORT = 0; // Will be dynamically assigned

    public interface FileTransferCallback {
        void onSuccess();

        void onFailure(String errorMessage);
    }

    public static void sendFile(String ipAddress, File file, FileTransferCallback callback) {
        // StrictMode
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

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
                Socket socket = new Socket(address_str, finalReceiverPort);

                // Retrieve the dynamically assigned port
                RECEIVER_PORT = socket.getLocalPort();
                Log.d("File RECEIVER_PORT", String.valueOf(socket.getLocalPort()));
                // Create input and output streams for file transfer
                BufferedInputStream bis = new BufferedInputStream(Files.newInputStream(file.toPath()));
                BufferedOutputStream bos = new BufferedOutputStream(socket.getOutputStream());

                // Read the file and send it in chunks
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = bis.read(buffer)) != -1) {
                    bos.write(buffer, 0, bytesRead);
                }

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
                ServerSocket serverSocket = new ServerSocket(receiverPort);
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

}
