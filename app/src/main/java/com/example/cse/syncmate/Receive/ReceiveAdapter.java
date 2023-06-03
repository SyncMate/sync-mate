package com.example.cse.syncmate.Receive;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

    public void addFile(String fileName) {
        receivedFiles.add(fileName);
        notifyItemInserted(receivedFiles.size() - 1);
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

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            fileNameTextView = itemView.findViewById(R.id.fileNameTextView);
        }

        public void bind(String fileName) {
            fileNameTextView.setText(fileName);
        }
    }
}

