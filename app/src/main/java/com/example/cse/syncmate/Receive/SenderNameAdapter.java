package com.example.cse.syncmate.Receive;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cse.syncmate.R;

public class SenderNameAdapter extends RecyclerView.Adapter<SenderNameAdapter.ViewHolder> {

    private String senderDeviceName;

    public SenderNameAdapter(String senderDeviceName) {
        this.senderDeviceName = senderDeviceName;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_receive, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Bind the sender device name to the TextView
        holder.senderNameTextView.setText(senderDeviceName);
    }

    @Override
    public int getItemCount() {
        return 1; // Since there is only one item
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView senderNameTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            senderNameTextView = itemView.findViewById(R.id.senderNameTextView);
        }
    }
}
