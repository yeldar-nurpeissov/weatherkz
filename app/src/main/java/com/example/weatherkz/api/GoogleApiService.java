package com.example.weatherkz.api;

import com.example.weatherkz.pojo.response.PlacesResponse;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GoogleApiService {
    @GET("/maps/api/place/autocomplete/json?&types=(cities)&components=country:kz&language=en&key=AIzaSyCn6rv4Rb1PIA3rrenRfDGIv5Q-gfyBRJg")
    Observable<PlacesResponse> getPlaceIds(@Query("input") String input);
}
