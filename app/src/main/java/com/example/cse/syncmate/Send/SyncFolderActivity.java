package com.example.cse.syncmate.Send;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.cse.syncmate.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class SyncFolderActivity extends AppCompatActivity {

    Button sendBtn;
    String ipAddress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync_folder);

        Intent intent = this.getIntent();
        if (intent != null) {
            ipAddress = intent.getStringExtra("ipAddress");
            Log.i("ipAddress", ipAddress);
        }

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                sendFile(ipAddress);
            }
        });

//        receiveFile();
    }


    private void sendFile(String ipAddress) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Get the IP address of the receiver device
                    InetAddress receiverAddress = InetAddress.getByName(ipAddress); // Replace with the IP address of the receiver device

                    // Create a socket connection to the receiver device
                    Socket socket = new Socket(receiverAddress, 8888); // 8888 is the port number

                    // Create a file object for the file to be sent
                    File file = new File(Environment.getExternalStorageDirectory(), "myfile.txt"); // Replace with the path to your file

                    // Create a file input stream for the file
                    FileInputStream fileInputStream = new FileInputStream(file);

                    // Get the output stream of the socket
                    OutputStream outputStream = socket.getOutputStream();

                    // Create a buffer to hold the data to be sent
                    byte[] buffer = new byte[1024];
                    int bytesRead;

                    // Read the file data into the buffer and send it over the socket
                    while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }

                    // Close the streams and socket
                    fileInputStream.close();
                    outputStream.close();
                    socket.close();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "File sent successfully!", Toast.LENGTH_SHORT).show();
                        }
                    });

                } catch (Exception e) {
                    // Handle exceptions here
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void receiveFile() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Create a server socket to listen for incoming connections
                    ServerSocket serverSocket = new ServerSocket(8888); // 8888 is the port number

                    // Wait for a client to connect
                    Socket socket = serverSocket.accept();

                    // Create a file object for the received file
                    File file = new File(Environment.getExternalStorageDirectory(), "received_file.txt"); // Replace with the desired path and file name

                    // Create an input stream to receive data from the socket
                    InputStream inputStream = socket.getInputStream();

                    // Create a file output stream to write the received data to the file
                    FileOutputStream fileOutputStream = new FileOutputStream(file);

                    // Create a buffer to hold the received data
                    byte[] buffer = new byte[1024];
                    int bytesRead;

                    // Read the data from the socket into the buffer and write it to the file
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        fileOutputStream.write(buffer, 0, bytesRead);
                    }

                    // Close the streams and socket
                    fileOutputStream.close();
                    inputStream.close();
                    socket.close();
                    serverSocket.close();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "File received successfully!", Toast.LENGTH_SHORT).show();
                        }
                    });

                } catch (Exception e) {
                    // Handle exceptions here
                    e.printStackTrace();
                }
            }
        }).start();
    }
}