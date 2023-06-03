package com.example.cse.syncmate.Receive;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.cse.syncmate.R;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ReceiveFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReceiveFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    //    private RecyclerView receivedFilesRecyclerView;
//    private ReceivedFilesAdapter receivedFilesAdapter;
    private static List<String> receivedFiles;
    private static ReceiveAdapter fileAdapter;
    private RecyclerView recyclerView;

    public ReceiveFragment() {
        // Required empty public constructor
    }


    // FileReceivedCallback interface for receiving file received events
    public interface FileReceivedCallback {
        void onFileReceived(String fileName);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ReceiveFragment.
     */
    // TODO: Rename and change types and number of parameters
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

        // Set up the RecyclerView with an empty list initially
        receivedFiles = new ArrayList<>();
        fileAdapter = new ReceiveAdapter(receivedFiles);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(fileAdapter);

        return view;
    }

    // Method to receive a file and update the UI
    private void receiveFile(String fileName) {
        receivedFiles.add(fileName);
        fileAdapter.addFile(fileName);
    }
    // Method to update the received files in the UI
    public static void updateReceivedFiles(List<String> files) {
        receivedFiles.clear();
        receivedFiles.addAll(files);
        fileAdapter.notifyDataSetChanged();
    }
}