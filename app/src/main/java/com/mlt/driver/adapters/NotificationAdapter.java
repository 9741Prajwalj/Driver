package com.mlt.driver.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mlt.driver.R;
import com.mlt.driver.fragments.Notifications;
import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {

    private List<Notifications> notificationList;  // Corrected to hold the custom Notifications class
    private final Context context;

    // Constructor to pass the context and notification list
    public NotificationAdapter(Context context, List<Notifications> notificationList) {
        this.context = context;
        this.notificationList = notificationList;
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for individual list items
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        Notifications notification = notificationList.get(position);  // Get the notification object at the given position

        // Set data from the Notifications object into the view holders
        holder.titleTextView.setText(notification.getTitle());
        holder.messageTextView.setText(notification.getMessage());
        holder.dateTextView.setText(notification.getDate());
    }

    @Override
    public int getItemCount() {
        // Return the size of the notification list
        return notificationList.size();
    }

    // ViewHolder class for holding the UI elements
    public static class NotificationViewHolder extends RecyclerView.ViewHolder {

        // Declare the TextViews for the notification title, message, and date
        TextView titleTextView, messageTextView, dateTextView;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);

            // Initialize the TextViews by finding them in the item layout
            titleTextView = itemView.findViewById(R.id.notificationTitle);
            messageTextView = itemView.findViewById(R.id.notificationMessage);
            dateTextView = itemView.findViewById(R.id.notificationDate);
        }
    }
}
