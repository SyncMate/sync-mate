package com.example.cse.syncmate;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class FolderAdapter extends ArrayAdapter<String> {

    private int iconResource1;
    private int iconResource2;
    public FolderAdapter(Context context, List<String> items, int iconResource1, int iconResource2) {
        super(context, 0, items);
        this.iconResource1 = iconResource1;
        this.iconResource2 = iconResource2;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View view, @NonNull ViewGroup parent) {
        if (view == null){
            view = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }
        ImageView folderImage = view.findViewById(R.id.folderImage);
        folderImage.setImageResource(iconResource1);

        TextView folderName = view.findViewById(R.id.folderName);
        folderName.setText(getItem(position));

        folderName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("TAG", "Folder created successfully");
            }
        });

        ImageButton syncImage = view.findViewById(R.id.sync_btn);
        syncImage.setImageResource(iconResource2);

        return view;
    }
}
