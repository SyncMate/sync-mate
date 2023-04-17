package com.dataflair.jobportal.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dataflair.jobportal.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class JobDetailsActivity extends AppCompatActivity {

    TextView companyNameTxt, jobTitleTxt, jobDescriptionTxt, jobSalaryTxt, startDateTxt, lastDateTxt;
    TextView totalOpeningsTxt, requiredSkillsTxt, additionalInfoTxt;

    EditText resumeLinkEditTxt;
    Button applyJobBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_details);

        //for Getting data from previous intent
        Bundle bundle = getIntent().getExtras();

        String companyName = bundle.getString("companyName");
        String jobTitle = bundle.getString("jobTitle");
        String jobDescription = bundle.getString("jobDescription");
        String jobSalary = bundle.getString("jobSalary");
        String startDate = bundle.getString("startDate");
        String lastDate = bundle.getString("lastDate");
        String totalOpenings = bundle.getString("totalOpenings");
        String requiredSkills = bundle.getString("requiredSkills");
        String additionalInfo = bundle.getString("additionalInfo");
        String adminId = bundle.getString("userId");
        String userId = GoogleSignIn.getLastSignedInAccount(getApplicationContext()).getId();
        String userName = GoogleSignIn.getLastSignedInAccount(getApplicationContext()).getDisplayName();


        //assigning all the android components addresses to perform appropriate action
        companyNameTxt = (TextView) findViewById(R.id.CompanyNameTxt);
        jobTitleTxt = (TextView) findViewById(R.id.JobTitleTxt);
        jobDescriptionTxt = (TextView) findViewById(R.id.JobDescriptionTxt);
        jobSalaryTxt = (TextView) findViewById(R.id.SalaryTxt);
        startDateTxt = (TextView) findViewById(R.id.JobStartDateTxt);
        lastDateTxt = (TextView) findViewById(R.id.LastDateToApplyTxt);
        totalOpeningsTxt = (TextView) findViewById(R.id.TotolNoOfOpeningsTxt);
        requiredSkillsTxt = (TextView) findViewById(R.id.RequiredSkillsTxt);
        additionalInfoTxt = (TextView) findViewById(R.id.AdditionalDataTxt);

        applyJobBtn = (Button) findViewById(R.id.ApplyJobBtn);
        resumeLinkEditTxt = (EditText) findViewById(R.id.ResumeLink);

        //setting the text to textView
        companyNameTxt.setText(companyName);
        jobTitleTxt.setText(jobTitle);
        jobDescriptionTxt.setText(jobDescription);
        jobSalaryTxt.setText(jobSalary);
        startDateTxt.setText(startDate);
        lastDateTxt.setText(lastDate);
        totalOpeningsTxt.setText(totalOpenings);
        requiredSkillsTxt.setText(requiredSkills);
        additionalInfoTxt.setText(additionalInfo);


        //On Click implementation to add the job application to the fireabase database
        applyJobBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String resumeLink = resumeLinkEditTxt.getText().toString();
                if (resumeLink.isEmpty()) {
                    //shows this toast if there empty field
                    Toast.makeText(getApplicationContext(), "Please,add Resume Link", Toast.LENGTH_SHORT).show();
                } else {
                    applyForJob(jobTitle, userId, adminId, resumeLink, companyName, userName);
                }

            }
        });


    }

    private void applyForJob(String jobTitle, String userId, String adminId, String resumeLink, String companyName, String userName) {

        //Hash map to store all the data in it
        HashMap<String, Object> applicaiton_Details = new HashMap<>();

        //key to generate unique value every time we apply for the job
        String key = FirebaseDatabase.getInstance().getReference().child("jobApplications").push().getKey();

        //adding all the details to hashmap
        applicaiton_Details.put("jobTitle", jobTitle);
        applicaiton_Details.put("adminId", adminId);
        applicaiton_Details.put("companyName", companyName);
        applicaiton_Details.put("userId", userId);
        applicaiton_Details.put("userName", userName);
        applicaiton_Details.put("resumeLink", resumeLink);


        //adding the job applicaiton to firebase
        FirebaseDatabase.getInstance().getReference().child("jobApplications").child(adminId).child(key)
                .updateChildren(applicaiton_Details).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull @org.jetbrains.annotations.NotNull Task<Void> task) {

                if (task.isSuccessful()) {

                    FirebaseDatabase.getInstance().getReference().child("jobApplications").child(userId).child(key)
                            .updateChildren(applicaiton_Details).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull @org.jetbrains.annotations.NotNull Task<Void> task) {

                            if (task.isSuccessful()) {

                                //shows this toast message after successfully adds the data to firabse
                                Toast.makeText(getApplicationContext(), "Successfully Applied For Job", Toast.LENGTH_SHORT).show();

                            }

                        }
                    });


                }

            }
        });

    }
}