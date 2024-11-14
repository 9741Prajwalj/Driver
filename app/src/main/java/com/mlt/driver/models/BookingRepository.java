package com.mlt.driver.models;

import androidx.lifecycle.MutableLiveData;


import com.mlt.driver.network.ApiService;

import java.util.List;

public class BookingRepository {

    private ApiService apiService;

//    public BookingRepository() {
//        apiService = new ApiService();
//    }

    // Method to fetch bookings based on the selected filter
//    public void fetchBookings(String endpoint, MutableLiveData<List<Booking>> bookingListLiveData) {
//        // Make an API request based on the selected endpoint (Upcoming, Completed, Cancelled)
//            apiService.getBookings(endpoint, new ApiService.ApiCallback<List<Booking>>() {
//            @Override
//            public void onSuccess(List<Booking> bookings) {
//                bookingListLiveData.setValue(bookings);
//            }
//
//            @Override
//            public void onFailure(String errorMessage) {
//                // Handle failure (e.g., show an error message)
//            }
//        });
//    }
}
