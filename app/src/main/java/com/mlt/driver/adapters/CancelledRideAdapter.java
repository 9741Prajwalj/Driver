package com.mlt.driver.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.mlt.driver.R;
import com.mlt.driver.models.CancelledRide;
import java.util.List;

public class CancelledRideAdapter extends RecyclerView.Adapter<CancelledRideAdapter.CancelledRideViewHolder> {
    private final Context context;
    private final List<CancelledRide> rideList;
    // Constructor
    public CancelledRideAdapter(Context context, List<CancelledRide> rideList) {
        this.context = context;
        this.rideList = rideList;
    }
    @NonNull
    @Override
    public CancelledRideViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.canceled_list_item, parent, false);
        Log.d("CancelledRideAdapter", "ViewHolder created for item.");
        return new CancelledRideViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull CancelledRideViewHolder holder, int position) {
        CancelledRide ride = rideList.get(position);
        // Binding data to the views
        holder.txtBookingId.setText(String.format("Booking ID: %d", ride.getBookingId()));
        holder.txtBookingDate.setText(String.format("Date: %s", ride.getBookDate()));
        holder.txtBookingTime.setText(String.format("Time: %s", ride.getBookTime()));
        holder.txtSourceAddress.setText(String.format("From: %s", ride.getSourceAddress()));
        holder.txtDestAddress.setText(String.format("To: %s", ride.getDestAddress()));
        holder.txtJourneyDate.setText(String.format("Journey Date: %s", ride.getJourneyDate()));
        holder.txtJourneyTime.setText(String.format("Time: %s", ride.getJourneyTime()));
        holder.txtRideStatus.setText(String.format("Status: %s", ride.getRideStatus()));
        // Change color based on ride status
        int statusColor = "Cancelled".equalsIgnoreCase(ride.getRideStatus()) ? android.R.color.holo_red_dark : android.R.color.black;
        holder.txtRideStatus.setTextColor(holder.txtRideStatus.getContext().getResources().getColor(statusColor));
    }
    @Override
    public int getItemCount() {
        return rideList.size();
    }
    // ViewHolder Class
    public static class CancelledRideViewHolder extends RecyclerView.ViewHolder {
        TextView txtBookingId, txtBookingDate, txtBookingTime, txtSourceAddress, txtDestAddress, txtJourneyDate, txtJourneyTime, txtRideStatus;
        public CancelledRideViewHolder(@NonNull View itemView) {
            super(itemView);
            // Initializing views
            txtBookingId = itemView.findViewById(R.id.txtBookingId);
            txtBookingDate = itemView.findViewById(R.id.txtBookDate);
            txtBookingTime = itemView.findViewById(R.id.txtBookTime);
            txtSourceAddress = itemView.findViewById(R.id.txtSourceAddress);
            txtDestAddress = itemView.findViewById(R.id.txtDestAddress);
            txtJourneyDate = itemView.findViewById(R.id.txtJourneyDate);
            txtJourneyTime = itemView.findViewById(R.id.txtJourneyTime);
            txtRideStatus = itemView.findViewById(R.id.txtRideStatus);
        }
    }
}