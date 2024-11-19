    package com.mlt.driver.network;

    import com.google.gson.JsonObject;
    //    import com.mlt.driver.models.DriverStatusLocationRequest;

    import java.util.Map;

    import okhttp3.RequestBody;
    import okhttp3.ResponseBody;
    import retrofit2.Call;
    import retrofit2.http.Body;
    import retrofit2.http.POST;
    import retrofit2.http.Path;


    public interface ApiService {



        // Define the endpoint for user login
        @POST("api/user-login")
        Call<ResponseBody> loginUser(@Body RequestBody requestBody);

        @POST("/api/change-availability")
        Call<JsonObject> updateDriverAvailability(@Body Map<String, Object> body);

        @POST("/api/cancel")
        Call<Void> sendCancellationReason(@Body String reasonData);

        @POST("{endpoint}")
        Call<ResponseBody> fetchDataFromBackend(@Body RequestBody requestBody, @Path("endpoint") String endpoint);


        // Get route from source to destination using Google Directions API
    //    @GET("directions/json")
    //    Call<RouteResponse> getRoute(
    //            @Query("origin") String origin,
    //            @Query("destination") String destination,
    //            @Query("key") String apiKey
    //    );

    }
