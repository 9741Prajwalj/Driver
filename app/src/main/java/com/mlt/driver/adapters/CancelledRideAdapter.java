package com.mlt.driver.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.mlt.driver.R;
import com.mlt.driver.models.CancelledRide;
import com.mlt.driver.models.Ride;
import java.util.List;

public class CancelledRideAdapter extends RecyclerView.Adapter<CancelledRideAdapter.ViewHolder> {

    private final List<CancelledRide> rideList;

    public CancelledRideAdapter(List<CancelledRide> rideList) {
        this.rideList = rideList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.canceled_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CancelledRide ride = rideList.get(position);

        holder.bookingId.setText("Booking ID: " + ride.getBookingId());
        holder.bookDate.setText("Date: " + ride.getBookDate());
        holder.sourceAddress.setText("Source: " + ride.getSourceAddress());
        holder.destAddress.setText("Destination: " + ride.getDestAddress());
        holder.journeyDate.setText("Journey Date: " + ride.getJourneyDate());
        holder.journeyTime.setText("Time: " + ride.getJourneyTime());
        holder.rideStatus.setText("Status: " + ride.getRideStatus());
    }

    @Override
    public int getItemCount() {
        return rideList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView bookingId, bookDate, sourceAddress, destAddress, journeyDate, journeyTime, rideStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            bookingId = itemView.findViewById(R.id.booking_id);
            bookDate = itemView.findViewById(R.id.booking_date);
            sourceAddress = itemView.findViewById(R.id.source_address);
            destAddress = itemView.findViewById(R.id.dest_address);
            journeyDate = itemView.findViewById(R.id.journey_date);
            journeyTime = itemView.findViewById(R.id.journey_time);
            rideStatus = itemView.findViewById(R.id.ride_status);
        }
    }
}

