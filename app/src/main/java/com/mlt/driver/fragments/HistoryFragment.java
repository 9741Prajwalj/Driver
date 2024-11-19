package com.mlt.driver.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mlt.driver.R;
import com.mlt.driver.adapters.BookingAdapter;
import com.mlt.driver.network.ApiService;
import com.mlt.driver.network.RetrofitClient;
import com.mlt.driver.viewmodel.BookingViewModel;

public class HistoryFragment extends Fragment {

    private RecyclerView recyclerView;
    private BookingAdapter bookingAdapter;
    private BookingViewModel bookingViewModel;
    private RadioGroup radioGroupBookingType;

    private ApiService apiService;  // ApiService instance

    public HistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewBookings);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        radioGroupBookingType = view.findViewById(R.id.radioGroupBookingType);

        bookingAdapter = new BookingAdapter(null);  // Initially set to null
        recyclerView.setAdapter(bookingAdapter);

        // Pass context to ViewModel
        bookingViewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @Override
            public <T extends ViewModel> T create(Class<T> modelClass) {
                return (T) new BookingViewModel(getContext());
            }
        }).get(BookingViewModel.class);

        apiService = RetrofitClient.getClient().create(ApiService.class); // Get ApiService instance from ApiClient

        // Observe LiveData to update the RecyclerView with the list of bookings
        bookingViewModel.getBookingList().observe(getViewLifecycleOwner(), bookings -> {
            bookingAdapter = new BookingAdapter(bookings); // Update adapter with new data
            recyclerView.setAdapter(bookingAdapter);
        });

        // Observe error messages
        bookingViewModel.getErrorLiveData().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });

        // Load bookings
        bookingViewModel.loadBookings("Upcoming");  // Default to upcoming bookings for initial load

        // Setup Go Button onClickListener
        view.findViewById(R.id.btnGo).setOnClickListener(v -> {
            int selectedId = radioGroupBookingType.getCheckedRadioButtonId();
            if (selectedId != -1) {
                RadioButton selectedRadioButton = view.findViewById(selectedId);
                String selectedType = selectedRadioButton.getText().toString();

                // Call API based on the selected radio button
                handleApiCall(selectedType);
            } else {
                Toast.makeText(getContext(), "Please select a booking type", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void handleApiCall(String bookingType) {
        // Define your API calls based on the selected radio button
        bookingViewModel.fetchBookingsForFilter(bookingType); // Use ViewModel to handle fetching
    }
}
