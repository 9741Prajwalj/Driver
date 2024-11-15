//package com.mlt.driver.fragments;
//
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.RadioButton;
//import android.widget.RadioGroup;
//import android.widget.Toast;
//
//import androidx.fragment.app.Fragment;
//import androidx.lifecycle.ViewModelProvider;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.mlt.driver.R;
//import com.mlt.driver.adapters.BookingAdapter;
//import com.mlt.driver.models.Booking;
//import com.mlt.driver.network.ApiService;
//import com.mlt.driver.viewmodel.BookingViewModel;
//
//import java.util.List;
//
//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;
//
//public class HistoryFragment extends Fragment {
//
//    private RecyclerView recyclerView;
//    private BookingAdapter bookingAdapter;
//    private BookingViewModel bookingViewModel;
//    private RadioGroup radioGroupBookingType;
//    private RadioButton selectedRadioButton;
//    private ApiService apiService;
//
//    public HistoryFragment() {
//        // Required empty public constructor
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        View view = inflater.inflate(R.layout.fragment_history, container, false);
//
//        // Initialize RecyclerView
//        recyclerView = view.findViewById(R.id.recyclerViewBookings);
//        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//
//        // Initialize Adapter (Initially null)
//        bookingAdapter = new BookingAdapter(null);
//        recyclerView.setAdapter(bookingAdapter);
//
//        // Initialize ViewModel
//        bookingViewModel = new ViewModelProvider(this).get(BookingViewModel.class);
//        apiService = ApiService.getInstance(); // Assuming you have a method to get ApiService instance
//
//
//        // Observe LiveData to update the RecyclerView with the list of bookings
//        bookingViewModel.getBookingList().observe(getViewLifecycleOwner(), bookings -> {
//            bookingAdapter = new BookingAdapter(bookings);
//            recyclerView.setAdapter(bookingAdapter);
//        });
//
//        // Load bookings (initial data load)
//        bookingViewModel.loadBookings();
//
//        // Initialize the RadioGroup and Go Button
//        radioGroupBookingType = view.findViewById(R.id.radioGroupBookingType);
//
//        // Set OnCheckedChangeListener to detect which radio button is selected
//        radioGroupBookingType.setOnCheckedChangeListener((group, checkedId) -> {
//            selectedRadioButton = view.findViewById(checkedId);
//        });
//
//        // Set up the Go button to trigger the appropriate endpoint
//        // Setup Go Button onClickListener
//        view.findViewById(R.id.btnGo).setOnClickListener(v -> {
//            int selectedId = radioGroupBookingType.getCheckedRadioButtonId();
//            if (selectedId != -1) {
//                RadioButton selectedRadioButton = view.findViewById(selectedId);
//                String selectedType = selectedRadioButton.getText().toString();
//
//                // Call API based on the selected radio button
//                handleApiCall(selectedType);
//            } else {
//                Toast.makeText(getContext(), "Please select a booking type", Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        return view;
//    }
//
//    private void handleApiCall(String bookingType) {
//        // Define your API calls based on the selected radio button
//        switch (bookingType) {
//            case "Upcoming":
//                // Make the API call for upcoming bookings
//                apiService.getUpcomingBookings().enqueue(new Callback<List<Booking>>() {
//                    @Override
//                    public void onResponse(Call<List<Booking>> call, Response<List<Booking>> response) {
//                        if (response.isSuccessful()) {
//                            bookingAdapter.updateData(response.body());
//                        } else {
//                            Toast.makeText(getContext(), "Failed to load upcoming bookings", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(Call<List<Booking>> call, Throwable t) {
//                        Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                });
//                break;
//
//            case "Completed":
//                // Make the API call for completed bookings
//                apiService.getCompletedBookings().enqueue(new Callback<List<Booking>>() {
//                    @Override
//                    public void onResponse(Call<List<Booking>> call, Response<List<Booking>> response) {
//                        if (response.isSuccessful()) {
//                            bookingAdapter.updateData(response.body());
//                        } else {
//                            Toast.makeText(getContext(), "Failed to load completed bookings", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(Call<List<Booking>> call, Throwable t) {
//                        Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                });
//                break;
//
//            case "Cancelled":
//                // Make the API call for cancelled bookings
//                apiService.getCancelledBookings().enqueue(new Callback<List<Booking>>() {
//                    @Override
//                    public void onResponse(Call<List<Booking>> call, Response<List<Booking>> response) {
//                        if (response.isSuccessful()) {
//                            bookingAdapter.updateData(response.body());
//                        } else {
//                            Toast.makeText(getContext(), "Failed to load cancelled bookings", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(Call<List<Booking>> call, Throwable t) {
//                        Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                });
//                break;
//
//            default:
//                Toast.makeText(getContext(), "Invalid selection", Toast.LENGTH_SHORT).show();
//                break;
//        }
//    }
//}
//    // Method to call the corresponding endpoint based on the selected radio button
//    private void callEndpoint(String endpoint) {
//        // You can call the appropriate API or function from your ViewModel here
//        bookingViewModel.fetchBookingsForFilter(endpoint);
//    }
