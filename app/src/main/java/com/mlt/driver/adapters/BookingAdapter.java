package com.mlt.driver.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mlt.driver.R;
import com.mlt.driver.models.Booking;

import java.util.List;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.BookingViewHolder> {
    private List<Booking> bookingList;
    // Constructor that initializes the adapter with the list of bookings
    public BookingAdapter(List<Booking> bookingList) {
        this.bookingList = bookingList;
    }
    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.booking_item, parent, false);
        return new BookingViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
        Booking booking = bookingList.get(position);
        holder.bookingId.setText("Booking ID: " + booking.getBookingId());
        holder.bookingDate.setText("Date: " + booking.getBookingDate());
        holder.sourceAddress.setText("Source: " + booking.getSourceAddress());
        holder.destAddress.setText("Destination: " + booking.getDestAddress());
        holder.journeyDate.setText("Journey Date: " + booking.getJourneyDate());
        holder.journeyTime.setText("Time: " + booking.getJourneyTime());
        holder.rideStatus.setText("Status: " + booking.getRideStatus());
        holder.totalKms.setText("Total Distance: " + booking.getTotalKms());
        holder.amount.setText("Amount: ₹" + booking.getAmount());
    }
    @Override
    public int getItemCount() {
        // Return the size of the list of bookings
        return bookingList != null ? bookingList.size() : 0;
    }

    // Method to update the data set dynamically
    public void updateData(List<Booking> newBookingList) {
        if (newBookingList != null) {
            this.bookingList = newBookingList;
            notifyDataSetChanged();  // Notify the adapter that the data set has changed
        }
    }
    public static class BookingViewHolder extends RecyclerView.ViewHolder {
        TextView bookingId, bookingDate, sourceAddress, destAddress, journeyDate, journeyTime, rideStatus, totalKms, amount;
        public BookingViewHolder(View itemView) {
            super(itemView);
            bookingId = itemView.findViewById(R.id.booking_id);
            bookingDate = itemView.findViewById(R.id.booking_date);
            sourceAddress = itemView.findViewById(R.id.source_address);
            destAddress = itemView.findViewById(R.id.dest_address);
            journeyDate = itemView.findViewById(R.id.journey_date);
            journeyTime = itemView.findViewById(R.id.journey_time);
            rideStatus = itemView.findViewById(R.id.ride_status);
            totalKms = itemView.findViewById(R.id.total_kms);
            amount = itemView.findViewById(R.id.amount);
        }
    }
}