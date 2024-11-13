package com.mlt.driver.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.mlt.driver.R;

public class HelpFragment extends Fragment {

    private LinearLayout btnclsup, btnchtsup, btnwebsup;
    private FrameLayout containerLayout, container_layout;

    // Cards for each support type
    private View phoneSupportCard, chatSupportCard, webSupportCard;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the current layout of HelpFragment
        View rootView = inflater.inflate(R.layout.fragment_help, container, false);

        // Find the LinearLayouts for each button
        btnclsup = rootView.findViewById(R.id.btnclsup);
        btnchtsup = rootView.findViewById(R.id.btnchtsup);
        btnwebsup = rootView.findViewById(R.id.btnwebsup);

        // FrameLayout is the container to replace UI content
        containerLayout = rootView.findViewById(R.id.container_layout);

        // Inflate the support card layouts
        phoneSupportCard = inflater.inflate(R.layout.phone_support_card, container, false);
        chatSupportCard = inflater.inflate(R.layout.chat_support_card, container, false);
        webSupportCard = inflater.inflate(R.layout.web_support_card, container, false);

        // Set OnClickListeners for each button
        btnclsup.setOnClickListener(v -> showSupportCard(phoneSupportCard));
        btnchtsup.setOnClickListener(v -> showSupportCard(chatSupportCard));
        btnwebsup.setOnClickListener(v -> showSupportCard(webSupportCard));

        return rootView;
    }

    private void showSupportCard(View supportCard) {
        // Remove all existing views in the container and add the selected support card
        containerLayout.removeAllViews();
        containerLayout.addView(supportCard);
    }

    private void closeSupportCard() {
        // Go back to the previous screen or hide the current support card
        containerLayout.removeAllViews();  // Close the current card
    }

    @Override
    public void onResume() {
        super.onResume();

        // Handle back press when no card is shown using OnBackPressedCallback (for compatibility)
        requireActivity().getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Check if any card is displayed, if so, close it
                if (containerLayout.getChildCount() > 0) {
                    containerLayout.removeAllViews();  // Close the current card
                } else {
                    // If no cards are displayed, navigate back to MapFragment
                    navigateToMapFragment();
                }
            }
        });
    }

    private void navigateToMapFragment() {
        // Use FragmentTransaction to replace HelpFragment with MapFragment
        FragmentTransaction transaction = requireFragmentManager().beginTransaction();
        MapFragment mapFragment = new MapFragment();  // Assuming MapFragment is the destination
        transaction.replace(R.id.fragment_container, mapFragment);  // Replace the current fragment with MapFragment
        transaction.addToBackStack(null);  // Add the transaction to back stack
        transaction.commit();
    }
}


