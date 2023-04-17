package com.dataflair.jobportal.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dataflair.jobportal.Adapters.AdminSelectedApplicationAdapter;
import com.dataflair.jobportal.Model.Model;
import com.dataflair.jobportal.R;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.firebase.database.FirebaseDatabase;


public class AdminSelectedFragment extends Fragment {

    RecyclerView recyclerView;
    AdminSelectedApplicationAdapter adapter;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AdminSelectedFragment() {
        // Required empty public constructor
    }

    public static AdminSelectedFragment newInstance(String param1, String param2) {
        AdminSelectedFragment fragment = new AdminSelectedFragment();
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
        View view = inflater.inflate(R.layout.fragment_admin_selected, container, false);

        //Assigning the Recyclerview to diaplay selected applications
        recyclerView = (RecyclerView) view.findViewById(R.id.AdminSelectedApplicationsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        String adminId = GoogleSignIn.getLastSignedInAccount(getContext()).getId();


        //Firebase Recycler Options to get the data form firebase database using model class and reference
        FirebaseRecyclerOptions<Model> options =
                new FirebaseRecyclerOptions.Builder<Model>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("selectedApplications").child(adminId), Model.class)
                        .build();

        adapter = new AdminSelectedApplicationAdapter(options);
        recyclerView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        //Starts listening for data from firebase when this fragment starts
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        //Stops listening for data from firebase
        adapter.stopListening();
    }
}