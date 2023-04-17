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

public class AdminSelectedApplicationAdapter extends FirebaseRecyclerAdapter<Model, AdminSelectedApplicationAdapter.Viewholder> {

    public AdminSelectedApplicationAdapter(@NonNull FirebaseRecyclerOptions<Model> options) {

        super(options);

    }

    @Override
    protected void onBindViewHolder(@NonNull AdminSelectedApplicationAdapter.Viewholder holder, int position, @NonNull Model model) {


        //for loading selected application user name into recycler view
        holder.txtTitle.setText(model.getUserName());

        //for loading the selected application job title
        holder.txtDesc.setText(model.getJobTitle());


    }


    @NonNull
    @Override
    public AdminSelectedApplicationAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        //the data objects are inflated into the xml file single_data_item
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_data_file, parent, false);
        return new AdminSelectedApplicationAdapter.Viewholder(view);
    }

    //we need view holder to hold each objet form recyclerview and to show it in recyclerview
    class Viewholder extends RecyclerView.ViewHolder {


        TextView txtTitle;
        TextView txtDesc;

        public Viewholder(@NonNull View itemView) {
            super(itemView);

            //assigning the address of the materials
            txtTitle = (TextView) itemView.findViewById(R.id.Title);
            txtDesc = (TextView) itemView.findViewById(R.id.Desc);
        }
    }

}
