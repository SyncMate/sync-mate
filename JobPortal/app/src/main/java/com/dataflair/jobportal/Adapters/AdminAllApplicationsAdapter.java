package com.dataflair.jobportal.Adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dataflair.jobportal.Activities.JobDetailsActivity;
import com.dataflair.jobportal.Model.Model;
import com.dataflair.jobportal.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class AdminAllApplicationsAdapter extends FirebaseRecyclerAdapter<Model, AdminAllApplicationsAdapter.Viewholder> {

    public AdminAllApplicationsAdapter(@NonNull FirebaseRecyclerOptions<Model> options) {

        super(options);

    }

    @Override
    protected void onBindViewHolder(@NonNull AdminAllApplicationsAdapter.Viewholder holder, int position, @NonNull Model model) {

        Context context = holder.txtTitle.getContext();

        //for loading applicant names into recycler view
        holder.txtTitle.setText(model.getUserName());

        //for loading jobTitle into recyclerview
        holder.txtDesc.setText(model.getJobTitle());

        //for getting applicant resume
        holder.resumeLink.setText(model.getResumeLink());


        //Onclick implementation to view user Resume
        holder.resumeLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //implicit intent  to open user Resume Url
                String url = model.getResumeLink();
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                context.startActivity(i);
            }
        });


        //implementing onclick Listener to accept the applicaiton
        holder.acceptJobApplicationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String adminId = model.getAdminId();
                String userId = model.getUserId();
                String jobTitle = model.getJobTitle();
                String companyName = model.getCompanyName();
                String userName = model.getUserName();

                //calling the method to add the data to firebase database
                acceptJobApplication(adminId, userId, jobTitle, companyName, userName, context);
            }
        });


    }

    private void acceptJobApplication(String adminId, String userId, String jobTitle, String companyName, String userName, Context context) {

        //hashMap to store data init
        HashMap<String, Object> applicaiton_Details = new HashMap<>();

        //Generating push key to add data to firebase with unique key
        String key = FirebaseDatabase.getInstance().getReference().child("selectedApplications").push().getKey();

        //adding the data to hashmap
        applicaiton_Details.put("jobTitle", jobTitle);
        applicaiton_Details.put("adminId", adminId);
        applicaiton_Details.put("companyName", companyName);
        applicaiton_Details.put("userId", userId);
        applicaiton_Details.put("userName", userName);


        //adding the data to firebase using hashmap under admin node
        FirebaseDatabase.getInstance().getReference().child("selectedApplications").child(adminId).child(key)
                .updateChildren(applicaiton_Details).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull @org.jetbrains.annotations.NotNull Task<Void> task) {

                if (task.isSuccessful()) {

                    //adding the data to firebase using hashmap under user node
                    FirebaseDatabase.getInstance().getReference().child("selectedApplications").child(userId).child(key)
                            .updateChildren(applicaiton_Details).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull @org.jetbrains.annotations.NotNull Task<Void> task) {

                            if (task.isSuccessful()) {

                                //shows this toast after accepting the job application
                                Toast.makeText(context, "Application accepted", Toast.LENGTH_SHORT).show();

                            }

                        }
                    });


                }

            }
        });
    }


    @NonNull
    @Override
    public AdminAllApplicationsAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        //the data objects are inflated into the xml file single_data_item
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_job_application_accept_file, parent, false);
        return new AdminAllApplicationsAdapter.Viewholder(view);
    }

    //we need view holder to hold each objet form recyclerview and to show it in recyclerview
    class Viewholder extends RecyclerView.ViewHolder {


        TextView txtTitle;
        TextView txtDesc;
        TextView resumeLink;
        Button acceptJobApplicationBtn;

        public Viewholder(@NonNull View itemView) {
            super(itemView);

            //asssiginig the address of the materials to show the job applications
            txtTitle = (TextView) itemView.findViewById(R.id.Title);
            txtDesc = (TextView) itemView.findViewById(R.id.Desc);
            resumeLink = (TextView) itemView.findViewById(R.id.ResumeLinkTxt);
            acceptJobApplicationBtn = (Button) itemView.findViewById(R.id.AcceptJobApplicationBtn);
        }
    }

}


