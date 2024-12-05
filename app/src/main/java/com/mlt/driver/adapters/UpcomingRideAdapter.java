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
import com.mlt.driver.R;
import com.mlt.driver.helper.SharedPreferencesManager;
import com.mlt.driver.models.PickupRequest;
import com.mlt.driver.models.PickupResponse;
import com.mlt.driver.models.UpcomingRide;
import com.mlt.driver.network.ApiService;
import com.mlt.driver.network.RetrofitClient;
import com.mlt.driver.utills.MyButton;
import com.mlt.driver.utills.MyTextView;
import java.util.List;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpcomingRideAdapter extends RecyclerView.Adapter<UpcomingRideAdapter.ViewHolder> {
    private static final String BASE_URL = "https://ets.mltcorporate.com";
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
        // Save booking details to SharedPreferences
        SharedPreferencesManager sharedPreferencesManager = SharedPreferencesManager.getInstance(context);
        String apiToken = sharedPreferencesManager.getString("api_token", "No Token Found");
        Log.d("UpcomingRideAdapter", "API Token Retrieved: " + apiToken);
        holder.btnCancel.setOnClickListener(v -> {
            if (rideActionListener != null) {
                rideActionListener.onCancelRide(ride);
            } else {
                Log.e("UpcomingRideAdapter", "RideActionListener is null on cancel");
            }
        });
        holder.btnStart.setOnClickListener(v -> {
            if (navController != null) {
                rideActionListener.onStartRide(ride);
                Log.d("UpcomingRideAdapter", "Navigating to HomeFragment using NavController");
                navController.navigate(R.id.nav_home); // Navigate to HomeFragment
            } else {
                Log.e("UpcomingRideAdapter", "NavController is null on start ride");
            }
        });
        holder.btnPickup.setOnClickListener(v -> {
            // Save data when button is clicked
            sharedPreferencesManager.saveString("booking_id", String.valueOf(ride.getBookingId()));
            sharedPreferencesManager.saveString("source_address", ride.getSourceAddress());
            sharedPreferencesManager.saveString("dest_address", ride.getDestAddress());
            sharedPreferencesManager.saveString("ride_status", ride.getRideStatus());

            Log.d("UpcomingRideAdapter", "Ride data saved in SharedPreferences");
            int bookingId = ride.getBookingId();
            // Create Retrofit instance
            ApiService apiService = RetrofitClient.getInstance(BASE_URL).create(ApiService.class);
            PickupRequest request = new PickupRequest(apiToken, bookingId);
            // Make API call
            apiService.getPickupDetails(request).enqueue(new Callback<PickupResponse>() {
                @Override
                public void onResponse(Call<PickupResponse> call, Response<PickupResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        PickupResponse pickupResponse = response.body();
                        if (pickupResponse.getSuccess() == 1) {
                            // Log or display the pickup details
                            PickupResponse.Data data = pickupResponse.getData();
                            Log.d("UpcomingRideAdapter", "BookingID: " + data.getBookingId());
                            Log.d("UpcomingRideAdapter", "Pickup Address: " + data.getPickupAddress());
                            Log.d("UpcomingRideAdapter", "Latitude: " + data.getPickupLat());
                            Log.d("UpcomingRideAdapter", "Longitude: " + data.getPickupLong());
                            Log.d("UpcomingRideAdapter", "Navigating to HomeFragment using NavController");

                            // Save pickup latitude and longitude to SharedPreferences
                            sharedPreferencesManager.saveString("pickup_lat", data.getPickupLat());
                            sharedPreferencesManager.saveString("pickup_long", data.getPickupLong());
                            // Navigate to PickupFragment using NavController
                            if (navController != null) {
                                navController.navigate(R.id.btnPickup);
                            }
                        } else {
                            Log.e("UpcomingRideAdapter", "API Error: " + pickupResponse.getMessage());
                        }
                    } else {
                        Log.e("UpcomingRideAdapter", "Response Error: " + response.message());
                    }
                }
                @Override
                public void onFailure(Call<PickupResponse> call, Throwable t) {
                    Log.e("UpcomingRideAdapter", "API Call Failed: " + t.getMessage());
                }
            });
        });
    }
    @Override
    public int getItemCount() {
        return rideList != null ? rideList.size() : 0;
    }
    public interface RideActionListener {
        void onCancelRide(UpcomingRide ride);
        void onStartRide(UpcomingRide ride);
        void onPickupRide(UpcomingRide ride);
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View btnPickup;
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
            btnPickup = itemView.findViewById(R.id.btnPickup);
        }
    }
}