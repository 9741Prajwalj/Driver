package com.mlt.driver.network;

import com.google.gson.JsonObject;

import java.util.Map;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {

    // Define the endpoint for user login
    @POST("api/user-login")
    Call<ResponseBody> loginUser(@Body RequestBody requestBody);

    @POST("/api/change-availability")
    Call<JsonObject> updateDriverAvailability(@Body Map<String, Object> body);

    @POST("/api/cancel")
    Call<Void> sendCancellationReason(@Body String reasonData);

    @POST("/api/upcoming-rides")
    Call<ResponseBody> getUpcomingRides(@Body RequestBody requestBody);

    @POST("/api/cancelled-rides")
    Call<ResponseBody> getCanceledRides(@Body RequestBody requestBody);

    @POST("/api/completed-rides")
    Call<ResponseBody> getCompletedRides(@Body RequestBody requestBody);

}
