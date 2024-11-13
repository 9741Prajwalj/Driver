package com.mlt.driver.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.mlt.driver.R;
import com.mlt.driver.network.ApiService;
import com.mlt.driver.network.RetrofitClient;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CancelDialogActivity extends AppCompatActivity {

    private RadioGroup radioGroup;
    private EditText reasonEditText;
    private Button submitButton;
    private String selectedReason;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cancel_dialog);

        radioGroup = findViewById(R.id.radioCancel);
        reasonEditText = findViewById(R.id.reason_etxt);
        submitButton = findViewById(R.id.submit_btn);

        // Example code to show how this activity is set up
        // For example, handling a button click to show the confirmation dialog
        findViewById(R.id.cancel_button).setOnClickListener(v -> onBackPressed());

        // Set listener for RadioGroup to capture the selected RadioButton
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton selectedRadioButton = findViewById(checkedId);
            selectedReason = selectedRadioButton.getText().toString();

            // Show EditText if "my reason is not listed" is selected
            if (checkedId == R.id.app) {
                reasonEditText.setVisibility(View.VISIBLE);
            } else {
                reasonEditText.setVisibility(View.GONE);
            }
        });

        // Set listener for submit button
        submitButton.setOnClickListener(v -> {
            if (selectedReason == null) {
                Toast.makeText(this, "Please select a reason", Toast.LENGTH_SHORT).show();
            } else {
                String additionalReason = reasonEditText.getVisibility() == View.VISIBLE
                        ? reasonEditText.getText().toString() : "";

                // Display Toast message
                Toast.makeText(this, "Submitting: " + selectedReason, Toast.LENGTH_SHORT).show();

                // Log the selected reason
                Log.d("CancelDialogActivity", "Selected Reason: " + selectedReason);

                // Send data to backend
                sendCancellationReasonToBackend(selectedReason, additionalReason);
            }
        });
    }

    private void sendCancellationReasonToBackend(String reason, String additionalReason) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("reason", reason);
            if (!additionalReason.isEmpty()) {
                jsonObject.put("additional_reason", additionalReason);
            }

            // Assuming ApiServer is your Retrofit interface
            ApiService apiService = RetrofitClient.getInstances().create(ApiService.class);
            Call<Void> call = apiService.sendCancellationReason(jsonObject.toString());

            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(CancelDialogActivity.this, "Reason submitted successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(CancelDialogActivity.this, "Failed to submit reason", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(CancelDialogActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error preparing data", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onBackPressed() {
        // Display a confirmation dialog before closing the activity
        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to cancel this action?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    // Close the activity if the user confirms
                    CancelDialogActivity.super.onBackPressed();
                })
                .setNegativeButton("No", (dialog, which) -> {
                    // Dismiss the dialog and keep the activity open
                    dialog.dismiss();
                })
                .create()
                .show();
    }

}

