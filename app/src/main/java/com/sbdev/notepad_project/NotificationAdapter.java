package com.sbdev.notepad_project;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {

    private Context context;
    private ArrayList<NotificationModel> arrayList;

    public NotificationAdapter(Context context, ArrayList<NotificationModel> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NotificationViewHolder(LayoutInflater.from(context).inflate(R.layout.notifications_item_layout,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {

        holder.notification.setText(arrayList.get(holder.getAdapterPosition()).notification);

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class NotificationViewHolder extends RecyclerView.ViewHolder {

        private TextView notification;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);

            notification=itemView.findViewById(R.id.notificationsItemText);

        }
    }

}
