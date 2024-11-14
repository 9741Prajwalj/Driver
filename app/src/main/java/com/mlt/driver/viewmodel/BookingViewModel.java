package com.mlt.driver.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;


import com.mlt.driver.models.Booking;
import com.mlt.driver.models.BookingRepository;

import java.util.List;

public class BookingViewModel extends ViewModel {

    private BookingRepository bookingRepository;
    private MutableLiveData<List<Booking>> bookingListLiveData;

    public BookingViewModel() {
        bookingRepository = new BookingRepository();
        bookingListLiveData = new MutableLiveData<>();
    }

    // Method to get the booking list
    public LiveData<List<Booking>> getBookingList() {
        return bookingListLiveData;
    }

    // Method to load bookings based on the selected filter
    public void loadBookings() {
        bookingRepository.fetchBookings("upcoming_endpoint", bookingListLiveData);
    }

    // Method to fetch bookings based on the selected filter (Upcoming, Completed, Cancelled)
    public void fetchBookingsForFilter(String endpoint) {
        bookingRepository.fetchBookings(endpoint, bookingListLiveData);
    }
}
