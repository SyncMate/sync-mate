package com.dataflair.jobportal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.dataflair.jobportal.Activities.AdminActivity;
import com.dataflair.jobportal.Activities.RoleActivity;
import com.dataflair.jobportal.Activities.StartingActivity;
import com.dataflair.jobportal.Fragments.DisplayJobFragment;
import com.dataflair.jobportal.Fragments.UserDashboardFragment;
import com.dataflair.jobportal.Fragments.UserProfileFragment;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    FrameLayout frameLayout;
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Assigning framelayout resource file to show appropriate fragment using address
        frameLayout = (FrameLayout) findViewById(R.id.UserFragmentContainer);
        //Assigining Bottomnavigaiton Menu
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.UserBottomNavigationView);
        Menu menuNav = bottomNavigationView.getMenu();
        //Setting the default fragment as HomeFragment
        getSupportFragmentManager().beginTransaction().replace(R.id.UserFragmentContainer, new DisplayJobFragment()).commit();
        //Calling the bottoNavigationMethod when we click on any menu item
        bottomNavigationView.setOnNavigationItemSelectedListener(bottomNavigationMethod);
    }


    private BottomNavigationView.OnNavigationItemSelectedListener bottomNavigationMethod =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull @org.jetbrains.annotations.NotNull MenuItem item) {

                    //Assigining Fragment as Null
                    Fragment fragment = null;
                    switch (item.getItemId()) {

                        //Shows the Appropriate Fragment by using id as address
                        case R.id.homeMenu:
                            fragment = new DisplayJobFragment();
                            break;
                        case R.id.Dashboard:
                            fragment = new UserDashboardFragment();
                            break;
                        case R.id.profileMenu:
                            fragment = new UserProfileFragment();
                            break;

                    }
                    //Sets the selected Fragment into the Framelayout
                    getSupportFragmentManager().beginTransaction().replace(R.id.UserFragmentContainer, fragment).commit();
                    return true;
                }
            };




    @Override
    protected void onStart() {
        super.onStart();
        //checking user already logined or not
        FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
        if(mUser==null)
        {
            //if user not signed in then we will redirect this activity to LoginActivity
            Intent intent=new Intent(MainActivity.this, StartingActivity.class);
            startActivity(intent);
        }else{
            //Checks for user Role and starts the appropriate activity
            String id = GoogleSignIn.getLastSignedInAccount(getApplicationContext()).getId();
            DatabaseReference reference= FirebaseDatabase.getInstance().getReference().child("users").child(id).child("role");
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String data=snapshot.getValue().toString();
                    if(data.equals("admin"))
                    {
                        Intent userintent=new Intent(getApplicationContext(), AdminActivity.class);
                        startActivity(userintent);
                        finish();

                    }
                    else if(data.equals("empty"))
                    {
                        Intent Adminintent=new Intent(getApplicationContext(), RoleActivity.class);
                        startActivity(Adminintent);
                        finish();

                    }else{
                        //Do Nothing
                    }


                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

}
