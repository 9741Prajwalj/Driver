package com.mlt.driver.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.mlt.driver.R;
import com.mlt.driver.models.Review;
import java.util.ArrayList;
import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {
    private List<Review> reviews;
    // Constructor to initialize the reviews list
    public ReviewAdapter() {
        this.reviews = reviews != null ? reviews : new ArrayList<>(); // Initialize with an empty list if null
    }
    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the item layout for each review
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_review, parent, false);
        return new ReviewViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        // Bind the review data to the view holder
        Review review = reviews.get(position);
        holder.bind(review);
    }
    @Override
    public int getItemCount() {
        return reviews != null ? reviews.size() : 0; // Return the number of reviews
    }
    // Update the review list if needed
    public void updateReviews(List<Review> newReviews) {
        reviews.clear();
        reviews.addAll(newReviews);
        notifyDataSetChanged();
    }
    // ViewHolder class to hold and bind review item views
    static class ReviewViewHolder extends RecyclerView.ViewHolder {
        private final TextView txtReview;
        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            txtReview = itemView.findViewById(R.id.txtReview);
        }
        public void bind(Review review) {
            txtReview.setText(review.getReviewText()); // Set the review text (use the appropriate getter)
        }
    }
}