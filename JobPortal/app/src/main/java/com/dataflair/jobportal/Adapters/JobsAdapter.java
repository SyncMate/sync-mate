package com.dataflair.jobportal.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dataflair.jobportal.Activities.JobDetailsActivity;
import com.dataflair.jobportal.Model.Model;
import com.dataflair.jobportal.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class JobsAdapter extends FirebaseRecyclerAdapter<Model, JobsAdapter.Viewholder> {

    public JobsAdapter(@NonNull FirebaseRecyclerOptions<Model> options) {

        super(options);

    }

    @Override
    protected void onBindViewHolder(@NonNull Viewholder holder, int position, @NonNull Model model) {

        Context context=holder.txtTitle.getContext();

        //for loading all job titles into recycler view
        holder.txtTitle.setText(model.getJobTitle());

        //for loading all the job salaries into recyclerview
        holder.txtDesc.setText(model.getJobSalary());

        holder.txtTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Getting all the job Details and assigning it to a string
                String companyName=model.getCompanyName().toString();
                String jobTitle=model.getJobTitle().toString();
                String jobDescription=model.getAboutJob().toString();
                String jobSalary=model.getJobSalary().toString();
                String startDate=model.getJobStartDate().toString();
                String lastDate=model.getJobLastDate().toString();
                String totalOpenings=model.getTotalOpenings().toString();
                String requiredSkills=model.getSkillsRequired().toString();
                String additionalInfo=model.getAddationalInfo().toString();
                String userId=model.getAdminId().toString();

                //Intent to show all the details of a particular job
                Intent intent=new Intent(context, JobDetailsActivity.class);

                //passing the job details to the next intent
                intent.putExtra("companyName",companyName);
                intent.putExtra("jobTitle",jobTitle);
                intent.putExtra("jobDescription",jobDescription);
                intent.putExtra("jobSalary",jobSalary);
                intent.putExtra("startDate",startDate);
                intent.putExtra("lastDate",lastDate);
                intent.putExtra("totalOpenings",totalOpenings);
                intent.putExtra("requiredSkills",requiredSkills);
                intent.putExtra("additionalInfo",additionalInfo);
                intent.putExtra("userId",userId);

                context.startActivity(intent);
            }
        });


    }


    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        //the data ojects are inflated into the xml file single_data_item
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_data_file, parent, false);
        return new Viewholder(view);
    }

    //we need view holder to hold each objet form recyclerview and to show it in recyclerview
    class Viewholder extends RecyclerView.ViewHolder {


        TextView txtTitle;
        TextView txtDesc;

        public Viewholder(@NonNull View itemView) {
            super(itemView);

            //asssiginig the address of the materials to show the available jobs
            txtTitle = (TextView) itemView.findViewById(R.id.Title);
            txtDesc = (TextView) itemView.findViewById(R.id.Desc);
        }
    }

}

