package com.dataflair.jobportal.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dataflair.jobportal.Adapters.JobsAdapter;
import com.dataflair.jobportal.Model.Model;
import com.dataflair.jobportal.R;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;

public class DisplayJobFragment extends Fragment {

    RecyclerView recyclerView;
    JobsAdapter jobsAdapter;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public DisplayJobFragment() {
    }

    public static DisplayJobFragment newInstance(String param1, String param2) {
        DisplayJobFragment fragment = new DisplayJobFragment();
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
        View view = inflater.inflate(R.layout.fragment_display_job, container, false);

        //Assigning the Recyclerview to display all jobs
        recyclerView = (RecyclerView) view.findViewById(R.id.JobsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        //Firebase Recycler Options to get the data form firebase database using model class and reference
        FirebaseRecyclerOptions<Model> options =
                new FirebaseRecyclerOptions.Builder<Model>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("jobs"), Model.class)
                        .build();


        jobsAdapter = new JobsAdapter(options);
        recyclerView.setAdapter(jobsAdapter);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        //Starts listening for data from firebase when this fragment starts
        jobsAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        //Stops listening for data from firebase
        jobsAdapter.stopListening();
    }
}