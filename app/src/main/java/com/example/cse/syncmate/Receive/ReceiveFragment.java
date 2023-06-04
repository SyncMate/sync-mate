package com.example.cse.syncmate.Receive;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cse.syncmate.R;

import java.util.ArrayList;
import java.util.List;

public class ReceiveFragment extends Fragment {

    private static List<String> receivedFiles;
    private static String sender;
    private static ReceiveAdapter fileAdapter;
    private static SenderNameAdapter senderNameAdapter;
    private RecyclerView recyclerView;
    private TextView senderDevice;

    public ReceiveFragment() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_receive, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);

        senderDevice = view.findViewById(R.id.senderNameTextView);

        // Set up the RecyclerView with an empty list initially
        receivedFiles = new ArrayList<>();
        sender = "";
        fileAdapter = new ReceiveAdapter(receivedFiles);
        senderNameAdapter = new SenderNameAdapter(sender);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(fileAdapter);
        senderDevice.setText(sender);
        return view;
    }

    // Method to update the received files in the UI
    public static void updateReceivedFiles(List<String> files) {
        if (receivedFiles != null && fileAdapter != null) {
            receivedFiles.clear();
            receivedFiles.addAll(files);
            fileAdapter.notifyDataSetChanged();
        }
    }

}