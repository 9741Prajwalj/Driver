//package com.mlt.driver.manager;
//
//import static com.mlt.driver.helper.SharedPreferencesManager.KEY_USER_ID;
//
//import android.content.Context;
//import android.util.Log;
//
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.mlt.driver.network.ApiService;
//import com.mlt.driver.network.RetrofitClient;
//
//import org.json.JSONObject;
//
//import okhttp3.MediaType;
//import okhttp3.RequestBody;
//import okhttp3.ResponseBody;
//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;
//
//public class UserStatusManager {
//    public static Context context;
//
//    public UserStatusManager(Context context) {
//        UserStatusManager.context = context;
//    }
//    // Method to update a generic status in both backend and Firebase
//    public static void updateStatus(int userId, int status) {
//        // Update status in backend
//        updateStatusInBackend(userId, status);
//
//        // Update status in Firebase
////        updateStatusInFirebase(userId, status);
//    }
//    // Helper method to update the status in backend
//    private static void updateStatusInBackend(int userId, int status) {
//        try {
//            JSONObject statusRequest = new JSONObject();
//            statusRequest.put(KEY_USER_ID, userId);
//            statusRequest.put("status", status);
//
//            RequestBody requestBody = RequestBody.create(
//                    MediaType.parse("application/json"), statusRequest.toString()
//            );
//
//            ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
////            Call<ResponseBody> call = apiService.updateStatus(requestBody);
//            Call<ResponseBody> call = apiService.updateStatus(userId,status);
//
//
//            call.enqueue(new Callback<ResponseBody>() {
//                @Override
//                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                    if (response.isSuccessful()) {
//                        Log.d("UserStatusManager", "Backend status updated to " + status);
//                    } else {
//                        Log.e("UserStatusManager", "Failed to update backend status");
//                    }
//                }
//
//                @Override
//                public void onFailure(Call<ResponseBody> call, Throwable t) {
//                    Log.e("UserStatusManager", "Backend status update failed - Error: " + t.getMessage(), t);
//                }
//            });
//        } catch (Exception e) {
//            Log.e("UserStatusManager", "Error creating JSON request", e);
//        }
//    }
//
//    // Helper method to update the status in Firebase
//    private static void updateStatusInFirebase(int userId, int status) {
//        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("users").child(String.valueOf(userId));
//        databaseRef.child("status").setValue(status).addOnCompleteListener(task -> {
//            if (task.isSuccessful()) {
//                Log.d("UserStatusManager", "Status updated in Firebase to " + status);
//            } else {
//                Log.e("UserStatusManager", "Failed to update status in Firebase");
//            }
//        });
//    }
//}
//
