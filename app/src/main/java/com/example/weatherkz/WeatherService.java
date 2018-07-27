package com.example.weatherkz;

import com.example.weatherkz.pojo.WeatherResponse;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherService {
    //    http://api.openweathermap.org
    @GET("/data/2.5/weather?units=metric&appid=6e572af63e1608796e8b92c05d05b23d")
    Observable<WeatherResponse> getForecastByLocation(@Query("lat") double latitude, @Query("lon") double longitude);
}
