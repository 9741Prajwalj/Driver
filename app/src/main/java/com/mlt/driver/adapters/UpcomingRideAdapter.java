package com.mlt.driver.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.mlt.driver.R;
import com.mlt.driver.fragments.PickupRoute;
import com.mlt.driver.models.UpcomingRide;
import com.mlt.driver.network.ApiService;
import com.mlt.driver.network.RetrofitClient;
import com.mlt.driver.utills.MyButton;
import com.mlt.driver.utills.MyTextView;
import java.util.List;
import de.hdodenhof.circleimageview.CircleImageView;

public class UpcomingRideAdapter extends RecyclerView.Adapter<UpcomingRideAdapter.ViewHolder> {
    private final Context context;
    private final List<UpcomingRide> rideList;
    private final RideActionListener rideActionListener;
    private final NavController navController; // Use NavController for navigation
    double currentLat = 0;
    double currentLng = 0;
    GoogleMap googleMap = null;

    public UpcomingRideAdapter(Context context, List<UpcomingRide> rideList, RideActionListener listener,NavController navController) {
        this.context = context;
        this.rideList = rideList;
        this.rideActionListener = listener;
        this.navController = navController;
        // Debug log to ensure NavController is not null
        if (this.navController == null) {
            Log.e("HistoryFragment", "NavController is null in HistoryFragment");
        } else {
            Log.d("HistoryFragment", "NavController initialized: " + navController);
        }
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
        holder.tvBookingId.setText(String.valueOf("Booking ID : " + ride.getBookingId()));
        holder.tvBookDate.setText("Journey Date : " + ride.getBookDate());
        holder.tvSource.setText("From : " + ride.getSourceAddress());
        holder.tvDestination.setText("To : " + ride.getDestAddress());
        holder.tvRideStatus.setText(ride.getRideStatus()); // Assuming ride status is a String
        holder.tvBookTime.setText("Book Time : " + ride.getBookTime());
        holder.tvJourneyTime.setText("Journey Time : " + ride.getJourneyTime());
        holder.btnCancel.setOnClickListener(v -> {
            if (rideActionListener != null) {
                rideActionListener.onCancelRide(ride);
            } else {
                Log.e("UpcomingRideAdapter", "RideActionListener is null on cancel");
            }
        });
        holder.btnStart.setOnClickListener(v -> {
            if (navController != null) {
                Log.d("UpcomingRideAdapter", "Navigating to HomeFragment using NavController");
                navController.navigate(R.id.nav_home); // Navigate to HomeFragment
                PickupRoute pickupRoute = new PickupRoute(context, RetrofitClient.getClient().create(ApiService.class), googleMap, "AIzaSyCI7CwlYJ6Qt5pQGW--inSsJmdEManW-K0");
                pickupRoute.fetchPickupLocation(ride.getBookingId(), new LatLng(currentLat, currentLng)); // Replace currentLat, currentLng with actual location
            } else {
                Log.e("UpcomingRideAdapter", "NavController is null on start ride");
            }
        });
    }
    @Override
    public int getItemCount() {
        return rideList != null ? rideList.size() : 0;
    }
    public interface RideActionListener {
        void onCancelRide(UpcomingRide ride);
        void onStartRide(UpcomingRide ride);
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView driverImage;
        MyTextView tvBookingId, tvBookDate, tvSource, tvDestination, tvRideStatus, tvBookTime, tvJourneyTime;
        MyButton btnCancel, btnStart;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
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
