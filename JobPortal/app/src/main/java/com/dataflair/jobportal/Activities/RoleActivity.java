package com.dataflair.jobportal.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.dataflair.jobportal.MainActivity;
import com.dataflair.jobportal.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.NotNull;

public class RoleActivity extends AppCompatActivity {

    Button jobSeekerBtn, adminBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_role);

        //Getting the userid of the user from gMail to store the user details under this id
        String userId = GoogleSignIn.getLastSignedInAccount(getApplicationContext()).getId();

        jobSeekerBtn = (Button) findViewById(R.id.JobSeekerBtn);
        adminBtn = (Button) findViewById(R.id.AdminBtn);

        //Implementing on clickListener to save the current users role
        jobSeekerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FirebaseDatabase.getInstance().getReference().child("users")
                        .child(userId)
                        .child("role")
                        .setValue("jobseeker").addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                });

            }
        });

        //Implementing on clickListener to save the current admin role
        adminBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FirebaseDatabase.getInstance().getReference().child("users")
                        .child(userId)
                        .child("role")
                        .setValue("admin").addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(getApplicationContext(), AdminActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                });

            }
        });
    }
}