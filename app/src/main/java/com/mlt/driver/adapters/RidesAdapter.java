package com.mlt.driver.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mlt.driver.R;
import com.mlt.driver.models.Ride;

import java.util.List;

public class RidesAdapter extends RecyclerView.Adapter<RidesAdapter.RideViewHolder> {

    private List<Ride> ridesList;

    public RidesAdapter(List<Ride> ridesList) {
        this.ridesList = ridesList;
    }

    @NonNull
    @Override
    public RideViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for each item in the RecyclerView
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ride, parent, false);
        return new RideViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RideViewHolder holder, int position) {
        // Get the ride data at the current position
        Ride ride = ridesList.get(position);
        holder.bind(ride); // Bind the ride data to the view holder
    }

    @Override
    public int getItemCount() {
        return ridesList.size(); // Return the total number of rides
    }

    class RideViewHolder extends RecyclerView.ViewHolder {
        private TextView lblDate;
        private TextView lblTime;
        private TextView lblAmount;

        public RideViewHolder(@NonNull View itemView) {
            super(itemView);
            lblDate = itemView.findViewById(R.id.lblDate); // Assuming these IDs exist in your item layout
            lblTime = itemView.findViewById(R.id.lblTime);
            lblAmount = itemView.findViewById(R.id.lblAmount);
        }

        public void bind(Ride ride) {
            lblDate.setText(ride.getDate()); // Set date
            lblTime.setText(ride.getTime()); // Set time (ensure your Ride class has this method)
            lblAmount.setText(ride.getAmount()); // Set amount
        }
    }
}
