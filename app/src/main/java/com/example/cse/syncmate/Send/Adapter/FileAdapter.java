package com.example.cse.syncmate.Send.Adapter;

import android.content.Context;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.cse.syncmate.R;

import java.io.File;
import java.util.List;

public class FileAdapter extends ArrayAdapter<String> {
    public FileAdapter(Context context, List<String> items) {
        super(context, 0, items);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View view, @NonNull ViewGroup parent) {
        if (view == null){
            view = LayoutInflater.from(getContext()).inflate(R.layout.file_list_item, parent, false);
        }

        String currentItem = getItem(position);
        String[] currentItemSplit = currentItem.split("\\.");
        String extension = currentItemSplit[currentItemSplit.length - 1];

        ImageView fileImage = view.findViewById(R.id.folderImage);

        // Set the appropriate icon based on the file type
        if (extension.equals("pdf")) {
            fileImage.setImageResource(R.drawable.baseline_picture_as_pdf_24);
        } else if (extension.equals("png") || extension.equals("jpg")) {
            fileImage.setImageResource(R.drawable.baseline_image_24);
        } else if (extension.equals("txt")) {
            fileImage.setImageResource(R.drawable.baseline_article_24);
        } else if (extension.equals("mp3")) {
            fileImage.setImageResource(R.drawable.baseline_audio_file_24);
        } else if (extension.equals("txt")) {
            fileImage.setImageResource(R.drawable.baseline_article_24);
        } else if (extension.equals("mp4")) {
            fileImage.setImageResource(R.drawable.baseline_video_file_24);
        } else {
            fileImage.setImageResource(R.drawable.baseline_insert_drive_file_24);
        }

        TextView folderName = view.findViewById(R.id.folderName);
        folderName.setText(getItem(position));

        return view;
    }
}
