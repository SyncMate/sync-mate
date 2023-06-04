package com.example.cse.syncmate.Receive;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cse.syncmate.R;

import java.util.List;

public class ReceiveAdapter extends RecyclerView.Adapter<ReceiveAdapter.ViewHolder> {

    private List<String> receivedFiles;
    public ReceiveAdapter(List<String> receivedFiles) {
        this.receivedFiles = receivedFiles;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_received_file, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String fileName = receivedFiles.get(position);
        holder.bind(fileName);
    }

    @Override
    public int getItemCount() {
        return receivedFiles.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView fileNameTextView;
        private ImageView fileImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            fileNameTextView = itemView.findViewById(R.id.fileNameTextView);
            fileImageView = itemView.findViewById(R.id.fileImageView);
        }

        public void bind(String fileName) {
            fileNameTextView.setText(fileName);

            // Get the file extension from the filename
            String fileExtension = getFileExtension(fileName);

            // Set the appropriate image based on the file extension
            int imageResId = getIconForExtension(fileExtension);
            fileImageView.setImageResource(imageResId);
        }

        private String getFileExtension(String fileName) {
            // Extract the file extension from the filename
            int extensionIndex = fileName.lastIndexOf(".");
            if (extensionIndex != -1 && extensionIndex < fileName.length() - 1) {
                return fileName.substring(extensionIndex + 1).toLowerCase();
            }
            return "";
        }

        private int getIconForExtension(String fileExtension) {
            // Map file extensions to corresponding image resources
            switch (fileExtension) {
                case "pdf":
                    return R.drawable.baseline_picture_as_pdf_24;
                case "txt":
                    return R.drawable.baseline_article_24;
                case "mp3":
                    return R.drawable.baseline_audio_file_24;
                case "mp4":
                    return R.drawable.baseline_video_file_24;
                case "jpeg":
                    return R.drawable.baseline_image_24;
                case "png":
                    return R.drawable.baseline_image_24;
                default:
                    return R.drawable.baseline_insert_drive_file_24;
            }
        }
    }

}

