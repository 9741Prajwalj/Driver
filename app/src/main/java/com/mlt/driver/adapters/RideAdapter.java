package com.mlt.driver.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mlt.driver.R;
import com.mlt.driver.models.Ride;
import com.mlt.driver.utills.MyTextView;

import java.util.List;

public class RideAdapter extends RecyclerView.Adapter<RideAdapter.RideViewHolder> {

    private Context context;
    private List<Ride> rideList;

    public RideAdapter(Context context, List<Ride> rideList) {
        this.context = context;
        this.rideList = rideList;
    }

    @NonNull
    @Override
    public RideViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.upcoming_list_item, parent, false);
        return new RideViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RideViewHolder holder, int position) {
        Ride ride = rideList.get(position);

        holder.tvBookingId.setText("Booking ID: " + ride.getBookingId());
        holder.tvSource.setText("From: " + ride.getSourceAddress());
        holder.tvDestination.setText("To: " + ride.getDestinationAddress());
        holder.tvJourneyDate.setText("Date & Time: " + ride.getJourneyDate() + " " + ride.getJourneyTime());
        holder.tvRideStatus.setText("Status: " + ride.getRideStatus());
    }

    @Override
    public int getItemCount() {
        return rideList.size();
    }

    public static class RideViewHolder extends RecyclerView.ViewHolder {
        MyTextView tvBookingId, tvSource, tvDestination, tvJourneyDate, tvRideStatus;

        public RideViewHolder(@NonNull View itemView) {
            super(itemView);
            tvBookingId = itemView.findViewById(R.id.booking_id);
            tvSource = itemView.findViewById(R.id.source_address);
            tvDestination = itemView.findViewById(R.id.dest_address);
            tvJourneyDate = itemView.findViewById(R.id.journey_date);
            tvRideStatus = itemView.findViewById(R.id.ride_status);
        }
    }
}

