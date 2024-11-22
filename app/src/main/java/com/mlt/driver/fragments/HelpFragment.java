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
import com.mlt.driver.R;

public class HelpFragment extends Fragment {

    private LinearLayout btnwebsup;
    private FrameLayout containerLayout;

    // Cards for each support type
    private View phoneSupportCard, chatSupportCard, webSupportCard;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the current layout of HelpFragment
        View rootView = inflater.inflate(R.layout.fragment_help, container, false);

        // Find the LinearLayouts for each button
        LinearLayout btnclsup = rootView.findViewById(R.id.btnclsup);
        LinearLayout btnchtsup = rootView.findViewById(R.id.btnchtsup);
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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Handle back press when no card is shown using OnBackPressedCallback (for compatibility)
        requireActivity().getOnBackPressedDispatcher().addCallback(requireActivity(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Check if any card is displayed, if so, close it
                if (containerLayout.getChildCount() > 0) {
                    containerLayout.removeAllViews();  // Close the current card
                } else {
                    // If no cards are displayed, simply go back to the previous fragment
                    // This is done by popping the back stack to the previous fragment
                    if (getFragmentManager() != null && getFragmentManager().getBackStackEntryCount() > 0) {
                        getFragmentManager().popBackStack();  // Pop the current fragment from the back stack
                    } else {
                        // If no fragments are in the back stack, you can close the activity or just do nothing
                        // For example, if you are inside an activity, you can do:
                        // requireActivity().onBackPressed(); // To go back to the previous activity
                    }
                }
            }
        });
    }

}



