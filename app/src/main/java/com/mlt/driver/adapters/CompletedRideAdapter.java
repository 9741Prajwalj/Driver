package com.mlt.driver.adapters;

/**
 * Created by Prajwal J.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.mlt.driver.R;
import com.mlt.driver.models.CompletedRide;
import java.util.List;

public class CompletedRideAdapter extends RecyclerView.Adapter<CompletedRideAdapter.ViewHolder> {
    private Context context;
    private List<CompletedRide> rideList;
    public CompletedRideAdapter(Context context, List<CompletedRide> rideList) {
        this.context = context;
        this.rideList = rideList;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.completed_list_item, parent, false);
        return new CompletedRideAdapter.ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CompletedRide ride = rideList.get(position);
        // Bind the views with data
        holder.bookingId.setText("Booking ID: " + ride.getBookingId());
        holder.rideStatus.setText("Status: " + ride.getStatus());
        holder.bookingDate.setText("Booking Date & Time: " + ride.getBookingDateTime());
        holder.journeyDate.setText("Journey Date & Time: " + ride.getJourneyDateTime());
        holder.sourceAddress.setText("Source: " + ride.getSourceAddress());
        holder.destAddress.setText("Destination: " + ride.getDestAddress());
        holder.totalKm.setText("Total Distance: " + ride.getTotalKm() + " km");
        holder.amount.setText("Amount: ₹" + ride.getAmount());
        // Additional Info (if necessary)
        holder.sourceTime.setText("Source Time: " + ride.getSourceTime());
        holder.destTime.setText("Destination Time: " + ride.getDestTime());
    }
    @Override
    public int getItemCount() {
        return rideList.size();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // Views corresponding to the layout IDs
        TextView bookingId, bookingDate, rideStatus, journeyDate, sourceAddress, destAddress, totalKm, amount;
        TextView sourceTime, destTime;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Initialize the views with updated IDs from the XML layout
            bookingId = itemView.findViewById(R.id.tv_booking_id); // Updated ID
            rideStatus = itemView.findViewById(R.id.tv_ride_status); // Updated ID
            bookingDate = itemView.findViewById(R.id.tv_booking_date_time); // Updated ID
            journeyDate = itemView.findViewById(R.id.tv_journey_date_time); // Updated ID
            sourceAddress = itemView.findViewById(R.id.tv_source_address); // Updated ID
            destAddress = itemView.findViewById(R.id.tv_dest_address); // Updated ID
            totalKm = itemView.findViewById(R.id.tv_total_kms); // Updated ID
            amount = itemView.findViewById(R.id.tv_amount); // Updated ID
            // Additional info
            sourceTime = itemView.findViewById(R.id.tv_source_time); // Updated ID
            destTime = itemView.findViewById(R.id.tv_dest_time); // Updated ID
        }
    }
}