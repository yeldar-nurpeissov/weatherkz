package com.example.weatherkz.api;

import com.example.weatherkz.pojo.response.WeatherResponse;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherService {
    @GET("/data/2.5/weather?units=metric&appid=6e572af63e1608796e8b92c05d05b23d")
    Observable<WeatherResponse> getForecastByCityNameAndCounty(@Query("q") String cityNameWithCounty);
}
