package com.example.cse.syncmate.Send.Adapter;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.cse.syncmate.R;

import java.util.List;

public class FileAdapter extends ArrayAdapter<String> {

    private int iconResource1;
    public FileAdapter(Context context, List<String> items, int iconResource1) {
        super(context, 0, items);
        this.iconResource1 = iconResource1;
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

        return view;
    }
}
