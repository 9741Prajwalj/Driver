package com.mlt.driver.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import com.mlt.driver.R;

public class Earning1Fragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private LinearLayout lnrTitle;
    private ImageView backArrow;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_earning1, container, false);

        // Initialize views
        TextView lblTarget = view.findViewById(R.id.lblTarget);
        TextView lblEarnings = view.findViewById(R.id.lblEarnings);
        RecyclerView rcvRides = view.findViewById(R.id.rcvRides);
        RelativeLayout errorLayout = view.findViewById(R.id.errorLayout);

        // Additional setup (like RecyclerView adapter) can be done here
        setupRecyclerView();

        return view;
    }
    private void setupRecyclerView() {
        // Set up the RecyclerView (adapter, layout manager, etc.)
        // For example:
        // rcvRides.setLayoutManager(new LinearLayoutManager(getContext()));
        // rcvRides.setAdapter(new YourAdapter());
    }
    // Additional methods for handling data can be added here
}
