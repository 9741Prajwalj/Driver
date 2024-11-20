package com.mlt.driver.viewmodel;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.mlt.driver.helper.SharedPreferencesManager;
import com.mlt.driver.models.Booking;
import com.mlt.driver.models.BookingRepository;

import java.util.List;

public class BookingViewModel extends ViewModel {
    private BookingRepository bookingRepository;
    private MutableLiveData<List<Booking>> bookingListLiveData;
    private MutableLiveData<String> errorLiveData;  // To handle errors
    private SharedPreferencesManager sharedPreferencesManager;
    public BookingViewModel(Context context) {
        sharedPreferencesManager = new SharedPreferencesManager(context);
        bookingRepository = new BookingRepository();
        bookingListLiveData = new MutableLiveData<>();
        errorLiveData = new MutableLiveData<>();
    }
    // Method to get the booking list
    public LiveData<List<Booking>> getBookingList() {
        return bookingListLiveData;
    }
    // Method to get error LiveData
    public LiveData<String> getErrorLiveData() {
        return errorLiveData;
    }
    // Method to load bookings (for upcoming, completed, cancelled)
    public void loadBookings(String bookingType) {
    String apiToken=sharedPreferencesManager.getApiToken();
        int driverId=sharedPreferencesManager.getUserId();
        String endpoint = getEndpointForBookingType(bookingType);  // Determine the endpoint based on the booking type
        if (endpoint != null) {
            bookingRepository.fetchBookings(endpoint, apiToken,  driverId, bookingListLiveData, errorLiveData);
        } else {
            errorLiveData.setValue("Invalid booking type");
        }
    }
    // Helper method to get the appropriate endpoint based on the booking type
    private String getEndpointForBookingType(String bookingType) {
        switch (bookingType) {
            case "Upcoming":
                return "api/upcoming-rides";  // Use your actual endpoint here
            case "Completed":
                return "api/completed-rides";  // Use your actual endpoint here
            case "Cancelled":
                return "api/cancelled-rides";  // Use your actual endpoint here
            default:
                return null;
        }
    }
    // Method to fetch bookings based on the selected filter (Upcoming, Completed, Cancelled)
    public void fetchBookingsForFilter(String bookingType) {
        loadBookings(bookingType);  // Simply call the loadBookings method with the selected type
    }
}
