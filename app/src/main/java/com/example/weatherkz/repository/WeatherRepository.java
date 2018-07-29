package com.example.weatherkz.repository;

import com.example.weatherkz.api.GoogleApiService;
import com.example.weatherkz.api.WeatherService;
import com.example.weatherkz.db.WeatherDao;
import com.example.weatherkz.db.WeatherDatabase;
import com.example.weatherkz.pojo.response.PlacesResponse;
import com.example.weatherkz.pojo.response.WeatherResponse;

import javax.inject.Inject;

import io.reactivex.Observable;

public class WeatherRepository {

    private final WeatherDao weatherDao;
    private final WeatherService weatherService;
    private final GoogleApiService googleApiService;
    private final WeatherDatabase database;

    @Inject
    public WeatherRepository(WeatherDao weatherDao,
                             WeatherService weatherService,
                             GoogleApiService googleApiService,
                             WeatherDatabase database) {
        this.weatherDao = weatherDao;
        this.weatherService = weatherService;
        this.googleApiService = googleApiService;
        this.database = database;
    }

    public Observable<PlacesResponse> getPlaceIds(String query) {
        return googleApiService.getPlaceIds(query);
    }

    public Observable<WeatherResponse> getForecastByCityNameAndCounty(String format) {
        return weatherService.getForecastByCityNameAndCounty(format);
    }
}
