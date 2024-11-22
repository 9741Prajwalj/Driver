package com.mlt.driver.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.mlt.driver.R;
import com.mlt.driver.models.UpcomingRide;
import com.mlt.driver.utills.MyButton;
import com.mlt.driver.utills.MyTextView;
import java.util.List;
import de.hdodenhof.circleimageview.CircleImageView;

public class UpcomingRideAdapter extends RecyclerView.Adapter<UpcomingRideAdapter.ViewHolder> {
    private final Context context;
    private final List<UpcomingRide> rideList;
    public UpcomingRideAdapter(Context context, List<UpcomingRide> rideList) {
        this.context = context;
        this.rideList = rideList;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the item layout
        View view = LayoutInflater.from(context).inflate(R.layout.upcoming_list_item, parent, false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Get the ride data at the current position
        UpcomingRide ride = rideList.get(position);
        // Set the data for the views in the layout
        holder.tvBookingId.setText(String.valueOf(ride.getBookingId()));
        holder.tvBookDate.setText(ride.getBookDate());
        holder.tvSource.setText(ride.getSourceAddress());
        holder.tvDestination.setText(ride.getDestAddress());
        // Set the ride status
        holder.tvRideStatus.setText(ride.getRideStatus()); // Assuming ride status is a String like "Upcoming"
        // Set the booking time and journey time
        holder.tvBookTime.setText(ride.getBookTime());
        holder.tvJourneyTime.setText(ride.getJourneyTime());
        // Set the buttons' onClick listeners
        holder.btnCancel.setOnClickListener(v -> {
            // Handle cancel ride action
        });
        holder.btnStart.setOnClickListener(v -> {
            // Handle start ride action
        });
    }
    @Override
    public int getItemCount() {
        return rideList.size();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView driverImage;
        MyTextView tvBookingId, tvBookDate, tvSource, tvDestination, tvRideStatus, tvBookTime, tvJourneyTime;
        MyButton btnCancel, btnStart;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Initialize views from the layout
            driverImage = itemView.findViewById(R.id.driver_image);
            tvBookingId = itemView.findViewById(R.id.booking_id);
            tvBookDate = itemView.findViewById(R.id.journey_date);
            tvSource = itemView.findViewById(R.id.source_address);
            tvDestination = itemView.findViewById(R.id.dest_address);
            tvRideStatus = itemView.findViewById(R.id.ride_status);
            tvBookTime = itemView.findViewById(R.id.book_time);
            tvJourneyTime = itemView.findViewById(R.id.journey_time);
            btnCancel = itemView.findViewById(R.id.btnCancel);
            btnStart = itemView.findViewById(R.id.btnStart);
        }
    }
}