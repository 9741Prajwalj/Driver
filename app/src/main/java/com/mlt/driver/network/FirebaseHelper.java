package com.mlt.driver.network;


import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseHelper {
    public static void updateStatusInFirebase(int userId, int status) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                .getReference("drivers").child(String.valueOf(userId));
        databaseReference.child("status").setValue(status)
                .addOnSuccessListener(aVoid -> Log.d("FirebaseHelper", "Firebase status updated successfully"))
                .addOnFailureListener(e -> Log.e("FirebaseHelper", "Firebase status update failed", e));
    }
}

