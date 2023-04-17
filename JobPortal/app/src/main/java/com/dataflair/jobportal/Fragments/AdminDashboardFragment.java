package com.dataflair.jobportal.Fragments;

import android.os.Bundle;

import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dataflair.jobportal.R;

public class AdminDashboardFragment extends Fragment {


    SwitchCompat mSwitch;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AdminDashboardFragment() {
        // Required empty public constructor
    }


    public static AdminDashboardFragment newInstance(String param1, String param2) {
        AdminDashboardFragment fragment = new AdminDashboardFragment();
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
        View view = inflater.inflate(R.layout.fragment_admin_dashboard, container, false);

        //Switch to navigate between fragments
        mSwitch = (SwitchCompat) view.findViewById(R.id.AdminDashBoardSwitch);
        mSwitch.setChecked(false);
        getFragmentManager().beginTransaction().replace(R.id.AdminDashBoardContainer, new AdminAllApplicationsFragment()).commit();
        mSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mSwitch.isChecked()) {
                    //Navigates to Selected Applications Fragment
                    getFragmentManager().beginTransaction().replace(R.id.AdminDashBoardContainer, new AdminSelectedFragment()).commit();
                } else {

                    //Navigates To All Applications fragment
                    getFragmentManager().beginTransaction().replace(R.id.AdminDashBoardContainer, new AdminAllApplicationsFragment()).commit();
                }
            }
        });

        return view;
    }
}