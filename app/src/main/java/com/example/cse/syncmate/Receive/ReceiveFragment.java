package com.example.cse.syncmate.Receive;

import android.os.Bundle;
import android.util.Log;
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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ReceiveFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReceiveFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    private static List<String> receivedFiles;
    private static String sender;
    private static ReceiveAdapter fileAdapter;
    private static SenderNameAdapter senderNameAdapter;
    private RecyclerView recyclerView;
    private TextView senderDevice;

    //    private SenderNameAdapter senderNameAdapter;
    public ReceiveFragment() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public static ReceiveFragment newInstance(String param1, String param2) {
        ReceiveFragment fragment = new ReceiveFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
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

    public static void senderDeviceNameReceived(String senderName) {
        Log.d("Received file", "CAME INSIDE senderDeviceNameReceived");
        sender = senderName;
        Log.d("Received file", senderName);
        senderNameAdapter.notifyDataSetChanged();
//        fileAdapter.notifyDataSetChanged();
    }

    void updateSenderName(String senderDeviceName) {
        senderDevice.setText(senderDeviceName);
    }

//    public void onSenderDeviceNameReceived(String senderDeviceName) {
//        // Update the UI with the sender device name
//        fileAdapter.setSenderDeviceName(senderDeviceName);
//        fileAdapter.notifyDataSetChanged();
//    }
}