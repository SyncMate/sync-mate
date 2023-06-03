package com.example.cse.syncmate.Send;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;

import com.example.cse.syncmate.R;
import com.example.cse.syncmate.Send.Adapter.FolderAdapter;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SendFragment extends Fragment {
//    private ImageButton createFolderBtn;
    private ListView folderListView;
    private List<String> folderNameList;
    private FolderAdapter listViewAdapter;
    private WatchService watchService;
    private ExecutorService executor;
    private String syncMateFolderPath;
    private Path syncMateDir;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_send, container, false);

        syncMateFolderPath = "/storage/emulated/0/Download/SyncMate/";
        syncMateDir = new File(syncMateFolderPath).toPath();

        // Create SyncMate folder in Download directory
        createSyncMateFolder(syncMateFolderPath);


//        createFolderBtn = view.findViewById(R.id.folder_create_button);
//        createFolderBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
////                FolderCreateFragment folderCreateFragment = new
////                        FolderCreateFragment();
////                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.send_frag, folderCreateFragment).commit();
//                Intent i = new Intent(getContext(), FolderCreateActivity.class);
//                startActivity(i);
//            }
//        });

        // Display folders in SyncMate folder
        folderNameList = viewFolders(syncMateFolderPath);

        folderListView = view.findViewById(R.id.list);

        listViewAdapter = new FolderAdapter(getContext(), folderNameList, R.drawable.baseline_folder_24, R.drawable.baseline_sync_24);
        folderListView.setAdapter(listViewAdapter);
//        folderListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                // TODO: view folder content
//            }
//        });

        /** Display changes in SyncMate Directory in real time */

        // Create a WatchService instance to monitor the SyncMate directory for new folders
        try {
            watchService = FileSystems.getDefault().newWatchService();
            // Register the syncMateDir path with watch service
            syncMateDir.register(watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Start a new thread pool to watch for changes
        executor = Executors.newFixedThreadPool(3);

        // Create a thread to monitor the WatchService
        executor.execute(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    WatchKey key;
                    try {
                        key = watchService.take();
                    } catch (InterruptedException e) {
                        return;
                    }

                    // Process all the WatchEvents for the WatchKey
                    for (WatchEvent<?> event : key.pollEvents()) {
                        Path filePath = (Path) event.context();
                        File file = new File(syncMateDir.toFile(), filePath.toString());
                        String folderName = file.getName();

                        if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
                            if (file.isDirectory()) {
                                folderNameList.add(folderName);
                                Log.d("Folder created", folderName);
                                if (getActivity() != null) {
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            listViewAdapter.notifyDataSetChanged();
                                        }
                                    });
                                }

                            }
                        } else if (event.kind() == StandardWatchEventKinds.ENTRY_DELETE) {
                            if (file.isDirectory()) {
                                folderNameList.remove(folderName);
                                Log.d("Folder deleted", folderName);
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        listViewAdapter.notifyDataSetChanged();
                                    }
                                });
                            }
                        } else if (event.kind() == StandardWatchEventKinds.ENTRY_MODIFY) {
                            if (file.isDirectory()) {
                                int index = folderNameList.indexOf(folderName);
                                if (index != -1) {
                                    folderNameList.set(index, folderName);
                                    Log.d("Folder modified", folderName);
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            listViewAdapter.notifyDataSetChanged();
                                        }
                                    });
                                }
                            }
                        }
                    }
                    //Reset the watch
                    boolean valid = key.reset();
                    if (!valid) {
                        break;
                    }
                }
                try {
                    watchService.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        return view;
    }

    private void createSyncMateFolder(String syncMateFolderPath) {
        File folder = new File(syncMateFolderPath);
        if (!folder.exists()) {
            boolean success = folder.mkdirs();
            if (success) {
                Log.d("TAG", "Folder created successfully");
            } else {
                Log.e("TAG", "Failed to create folder");
            }
        }
    }

    private List<String> viewFolders(String syncMateFolderPath) {
        File directory = new File(syncMateFolderPath);
        List<String> folderList = new ArrayList<>();

        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory() & !file.getName().equals(".sync")) {
                        folderList.add(file.getName());
                        Log.d("TAG", "Folder name: " + file.getName());
                    }
                }
            }
        }
        return folderList;
    }
}


