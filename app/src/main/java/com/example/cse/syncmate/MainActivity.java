package com.example.cse.syncmate;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cse.syncmate.History.HistoryFragment;
import com.example.cse.syncmate.Receive.FileReceiver;
import com.example.cse.syncmate.Receive.ReceiveFragment;
import com.example.cse.syncmate.Send.SendFragment;
import com.example.cse.syncmate.Settings.SettingsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity
        implements BottomNavigationView
        .OnNavigationItemSelectedListener {

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navigator);

        bottomNavigationView
                = findViewById(R.id.bottomNavigationView);

        bottomNavigationView
                .setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.send);

        // Call the FileReceiver main method in a separate thread
        new Thread(new Runnable() {
            @Override
            public void run() {
                FileReceiver.main(new String[]{});
            }
        }).start();
    }
    SendFragment send = new SendFragment();
    ReceiveFragment receive = new ReceiveFragment();
    HistoryFragment history = new HistoryFragment();
    SettingsFragment settings = new SettingsFragment();

    @Override
    public boolean
    onNavigationItemSelected(@NonNull MenuItem item)
    {

        switch (item.getItemId()) {
            case R.id.send:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.flFragment, send)
                        .commit();
                return true;

            case R.id.receive:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.flFragment, receive)
                        .commit();
                return true;

            case R.id.history:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.flFragment, history)
                        .commit();
                return true;

            case R.id.settings:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.flFragment, settings)
                        .commit();
                return true;
        }
        return false;
    }
}
