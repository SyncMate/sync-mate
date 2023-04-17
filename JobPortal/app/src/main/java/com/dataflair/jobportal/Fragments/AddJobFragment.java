package com.dataflair.jobportal.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.dataflair.jobportal.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class AddJobFragment extends Fragment {

    EditText companyNameEditTxt, jobTitleEditTxt, jobSalaryEditTxt, jobStartDateEditTxt, jobLastDateEditTxt;
    EditText totalOpeningsEditTxt, aboutJobEditTxt, skillsRequiredEditTxt, addationalInfoEditTxt;
    Button addJobBtn;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private String mParam1;
    private String mParam2;

    public AddJobFragment() {
        // Required empty public constructor
    }

    public static AddJobFragment newInstance(String param1, String param2) {
        AddJobFragment fragment = new AddJobFragment();
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
        View view = inflater.inflate(R.layout.fragment_add_job, container, false);

        //Assigning all the Addresses of the Android Materials to get  Job Details
        companyNameEditTxt = (EditText) view.findViewById(R.id.CompanyNameEditTxt);
        jobTitleEditTxt = (EditText) view.findViewById(R.id.JobTitleEditTxt);
        jobSalaryEditTxt = (EditText) view.findViewById(R.id.JobSalaryEditTxt);
        jobStartDateEditTxt = (EditText) view.findViewById(R.id.JobStartDateEditTxt);
        jobLastDateEditTxt = (EditText) view.findViewById(R.id.JobLastDateEditTxt);
        totalOpeningsEditTxt = (EditText) view.findViewById(R.id.TotalOpeningsEditTxt);
        aboutJobEditTxt = (EditText) view.findViewById(R.id.AboutJobEditTxt);
        skillsRequiredEditTxt = (EditText) view.findViewById(R.id.SkillsRequiredEditTxt);
        addationalInfoEditTxt = (EditText) view.findViewById(R.id.AddationalInfoEditTxt);

        //AddJob onClick Implementation to add Job Details To Firebase
        addJobBtn = (Button) view.findViewById(R.id.AddJobBtn);
        addJobBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String CompanyName = companyNameEditTxt.getText().toString();
                String JobTitle = jobTitleEditTxt.getText().toString();
                String JobSalary = jobSalaryEditTxt.getText().toString();
                String JobStartDate = jobStartDateEditTxt.getText().toString();
                String JobLastDate = jobLastDateEditTxt.getText().toString();
                String TotalOpenings = totalOpeningsEditTxt.getText().toString();
                String AboutJob = aboutJobEditTxt.getText().toString();
                String SkillsRequired = skillsRequiredEditTxt.getText().toString();
                String AddationalInfo = addationalInfoEditTxt.getText().toString();
                if (CompanyName.isEmpty() || JobTitle.isEmpty() || JobSalary.isEmpty() || JobStartDate.isEmpty() || JobLastDate.isEmpty() || TotalOpenings.isEmpty() || AboutJob.isEmpty() || SkillsRequired.isEmpty()) {
                    Toast.makeText(getContext(), "Please,Enter All Details", Toast.LENGTH_SHORT).show();
                } else {
                    addJobDetailsToDatabase(CompanyName, JobTitle, JobSalary, JobStartDate, JobLastDate, TotalOpenings, AboutJob, SkillsRequired, AddationalInfo);
                }
            }
        });
        return view;
    }

    private void addJobDetailsToDatabase(String companyName, String jobTitle, String jobSalary, String jobStartDate, String jobLastDate, String totalOpenings, String aboutJob, String skillsRequired, String addationalInfo) {


        //Hashmap to store job details
        HashMap<String, Object> job_details = new HashMap<>();

        //Getting admin id
        String adminId = GoogleSignIn.getLastSignedInAccount(getContext()).getId();

        //Generating unique key
        String key = FirebaseDatabase.getInstance().getReference().child("jobs").push().getKey();

        //Adding job details to hashmap
        job_details.put("companyName", companyName);
        job_details.put("jobTitle", jobTitle);
        job_details.put("jobSalary", jobSalary);
        job_details.put("jobStartDate", jobStartDate);
        job_details.put("jobLastDate", jobLastDate);
        job_details.put("totalOpenings", totalOpenings);
        job_details.put("aboutJob", aboutJob);
        job_details.put("skillsRequired", skillsRequired);
        job_details.put("addationalInfo", addationalInfo);
        job_details.put("adminId", adminId);


        //Adding job details to fireabase
        FirebaseDatabase.getInstance().getReference().child("jobs")
                .child(key)
                .updateChildren(job_details).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull @org.jetbrains.annotations.NotNull Task<Void> task) {

                if (task.isSuccessful()) {

                    //Setting the edit text to blank  after successfully adding the data to fireabse
                    Toast.makeText(getContext(), "Job Details Added Successfully", Toast.LENGTH_SHORT).show();
                    companyNameEditTxt.setText("");
                    jobTitleEditTxt.setText("");
                    jobSalaryEditTxt.setText("");
                    jobStartDateEditTxt.setText("");
                    jobLastDateEditTxt.setText("");
                    totalOpeningsEditTxt.setText("");
                    aboutJobEditTxt.setText("");
                    skillsRequiredEditTxt.setText("");
                    addationalInfoEditTxt.setText("");

                }

            }
        });


    }
}