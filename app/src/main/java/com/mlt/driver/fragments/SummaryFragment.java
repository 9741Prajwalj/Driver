package com.mlt.driver.fragments;

/**
 * Created by Prajwal J.
 */

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.mlt.driver.R;
import com.mlt.driver.utills.MyTextView;

public class SummaryFragment extends Fragment {

    private TextView noOfRidesTxt;
    private MyTextView currencyTxt;
    private TextView revenueTxt;
    private TextView scheduleTxt;
    private TextView cancelTxt;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_summary, container, false);

        noOfRidesTxt = view.findViewById(R.id.no_of_rides_txt);
        currencyTxt = view.findViewById(R.id.currency_txt);
        revenueTxt = view.findViewById(R.id.revenue_txt);
        scheduleTxt = view.findViewById(R.id.schedule_txt);
        cancelTxt = view.findViewById(R.id.cancel_txt);

        // Set up dummy data
        setUpData();

        return view;
    }
    private void setUpData() {
        // Example data; in practice, replace this with dynamic data from your model
        noOfRidesTxt.setText("10"); // Total rides
        currencyTxt.setText("₹"); // Currency symbol
        revenueTxt.setText("200.00"); // Revenue amount
        scheduleTxt.setText("5"); // Scheduled rides
        cancelTxt.setText("2"); // Cancelled rides
    }
}
