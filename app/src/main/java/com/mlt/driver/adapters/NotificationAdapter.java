package com.mlt.driver.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.mlt.driver.R;
import com.mlt.driver.models.NotificationItem;
import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {
    private final List<NotificationItem> notificationList;
    private final Context context;
    public NotificationAdapter(Context context, List<NotificationItem> notificationList) {
        this.context = context;
        this.notificationList = notificationList;
    }
    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_notification, parent, false);
        return new NotificationViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        NotificationItem notification = notificationList.get(position);
        // Bind notification data to the views
        holder.titleTextView.setText(notification.getTitle());
        holder.messageTextView.setText(notification.getMessage());
        holder.dateTextView.setText(notification.getDate());
    }
    @Override
    public int getItemCount() {
        return notificationList.size();
    }
    public void updateList(List<NotificationItem> newNotificationList) {
        this.notificationList.clear();
        this.notificationList.addAll(newNotificationList);
        notifyDataSetChanged();
    }
    static class NotificationViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, messageTextView, dateTextView;
        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.notificationTitle);
            messageTextView = itemView.findViewById(R.id.notificationMessage);
            dateTextView = itemView.findViewById(R.id.notificationDate);
        }
    }
}
