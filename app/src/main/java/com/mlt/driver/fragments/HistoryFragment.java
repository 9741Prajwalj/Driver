package com.mlt.driver.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mlt.driver.R;
import com.mlt.driver.adapters.BookingAdapter;
import com.mlt.driver.viewmodel.BookingViewModel;

import java.util.List;

public class HistoryFragment extends Fragment {

    private RecyclerView recyclerView;
    private BookingAdapter bookingAdapter;
    private BookingViewModel bookingViewModel;
    private RadioGroup radioGroupBookingType;
    private RadioButton selectedRadioButton;

    public HistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.recyclerViewBookings);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize Adapter (Initially null)
        bookingAdapter = new BookingAdapter(null);
        recyclerView.setAdapter(bookingAdapter);

        // Initialize ViewModel
        bookingViewModel = new ViewModelProvider(this).get(BookingViewModel.class);

        // Observe LiveData to update the RecyclerView with the list of bookings
        bookingViewModel.getBookingList().observe(getViewLifecycleOwner(), bookings -> {
            bookingAdapter = new BookingAdapter(bookings);
            recyclerView.setAdapter(bookingAdapter);
        });

        // Load bookings (initial data load)
        bookingViewModel.loadBookings();

        // Initialize the RadioGroup and Go Button
        radioGroupBookingType = view.findViewById(R.id.radioGroupBookingType);

        // Set OnCheckedChangeListener to detect which radio button is selected
        radioGroupBookingType.setOnCheckedChangeListener((group, checkedId) -> {
            selectedRadioButton = view.findViewById(checkedId);
        });

        // Set up the Go button to trigger the appropriate endpoint
        view.findViewById(R.id.btnGo).setOnClickListener(v -> {
            if (selectedRadioButton != null) {
                String selectedFilter = selectedRadioButton.getText().toString();
                // Call different endpoints based on the selected radio button
                switch (selectedFilter) {
                    case "Upcoming":
                        callEndpoint("upcoming_endpoint");
                        break;
                    case "Completed":
                        callEndpoint("completed_endpoint");
                        break;
                    case "Cancelled":
                        callEndpoint("cancelled_endpoint");
                        break;
                    default:
                        Toast.makeText(getContext(), "Please select a booking type", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });

        return view;
    }

    // Method to call the corresponding endpoint based on the selected radio button
    private void callEndpoint(String endpoint) {
        // You can call the appropriate API or function from your ViewModel here
        bookingViewModel.fetchBookingsForFilter(endpoint);
    }
}
