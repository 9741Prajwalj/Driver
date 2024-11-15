    package com.mlt.driver.network;

    import com.google.gson.JsonObject;
    import com.mlt.driver.models.Booking;
    import com.mlt.driver.models.DriverLocationRequest;
//    import com.mlt.driver.models.DriverStatusLocationRequest;
    import com.mlt.driver.models.TripDetails;

    import java.util.List;
    import java.util.Map;

    import okhttp3.RequestBody;
    import okhttp3.ResponseBody;
    import retrofit2.Call;
    import retrofit2.http.Body;
    import retrofit2.http.Field;
    import retrofit2.http.GET;
    import retrofit2.http.Header;
    import retrofit2.http.POST;


    public interface ApiService {



        // Define the endpoint for user login
        @POST("api/user-login")
        Call<ResponseBody> loginUser(@Body RequestBody requestBody);

        @POST("/api/change-availability")
        Call<JsonObject> updateDriverAvailability(@Body Map<String, Object> body);

        @POST("/api/cancel")
        Call<Void> sendCancellationReason(@Body String reasonData);

        @POST("api/upcoming-bookings")
        Call<List<Booking>> getUpcomingBookings();

        @POST("api/completed-bookings")
        Call<List<Booking>> getCompletedBookings();

        @POST("api/cancelled-bookings")
        Call<List<Booking>> getCancelledBookings();


        // Get route from source to destination using Google Directions API
    //    @GET("directions/json")
    //    Call<RouteResponse> getRoute(
    //            @Query("origin") String origin,
    //            @Query("destination") String destination,
    //            @Query("key") String apiKey
    //    );

    }
