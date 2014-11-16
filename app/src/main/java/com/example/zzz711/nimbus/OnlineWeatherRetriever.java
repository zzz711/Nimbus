package com.example.zzz711.nimbus;

import retrofit.http.GET;
import retrofit.http.Path;

/**
 * @author Margo
 * @version 11/15/2014
 * This provides the functionality to grab JSON data and fills in the fields of JsonWeatherResponse
 */

public interface OnlineWeatherRetriever {
    @GET("/weather.ashx?q={location}&format=json&num_of_days={days}&key={key}")
    //example URL to be constructed: api.worldweatheronline.com/free/v2/weather.ashx?
    //  q=London
    //  &format=json
    //  &num_of_days=5
    //  &key=9cbd89fade0170bb8102ed4296968

    JsonWeatherResponse getWeather(@Path("location") String location, @Path("days") int days, @Path("keys") String key);
}




