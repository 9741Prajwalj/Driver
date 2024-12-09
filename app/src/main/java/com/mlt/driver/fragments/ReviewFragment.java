package com.mlt.driver.fragments;

/**
 * Created by Prajwal J.
 */

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mlt.driver.R;
import com.mlt.driver.adapters.ReviewAdapter;

public class ReviewFragment extends Fragment {

    private RecyclerView recReview;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_review_activity, container, false);
        // Initialize views
        recReview = view.findViewById(R.id.recReview);
        // Set up the RecyclerView
        setupRecyclerView();
        return view;
    }
    private void setupRecyclerView() {
        // Create a layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recReview.setLayoutManager(layoutManager);
        // Set adapter for the RecyclerView
        // Replace `ReviewAdapter` with your actual adapter class
        ReviewAdapter adapter = new ReviewAdapter(); // Assume you have an adapter class
        recReview.setAdapter(adapter);
    }
}
