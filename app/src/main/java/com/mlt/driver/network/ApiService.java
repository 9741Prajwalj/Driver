package com.mlt.driver.network;

import com.mlt.driver.models.TripDetails;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.GET;
import retrofit2.http.POST;


public interface ApiService {



    // Define the endpoint for user login
    @POST("api/user-login")
    Call<ResponseBody> loginUser(@Body RequestBody requestBody);

    @POST("/api/change-availability")
    Call<ResponseBody> updateStatus(@Field("user_id") int userId, @Field("status") int status);

    @GET("path/to/your/endpoint")  // Specify your endpoint here
    Call<TripDetails> getTripDetails();

    @POST("/api/cancel")
    Call<Void> sendCancellationReason(@Body String reasonData);

    // Get route from source to destination using Google Directions API
//    @GET("directions/json")
//    Call<RouteResponse> getRoute(
//            @Query("origin") String origin,
//            @Query("destination") String destination,
//            @Query("key") String apiKey
//    );

}
