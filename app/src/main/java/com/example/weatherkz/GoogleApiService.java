package com.example.weatherkz;

import com.example.weatherkz.pojo.PlaceDetailResponse;
import com.example.weatherkz.pojo.PlacesResponse;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GoogleApiService {
    //    https://maps.googleapis.com
    @GET("/maps/api/place/details/json?fields=name,geometry&key=AIzaSyCn6rv4Rb1PIA3rrenRfDGIv5Q-gfyBRJg")
    Observable<PlaceDetailResponse> getPlaceLocation(@Query("placeid") String placeId);

    @GET("/maps/api/place/autocomplete/json?&types=(cities)&components=country:kz&key=AIzaSyCn6rv4Rb1PIA3rrenRfDGIv5Q-gfyBRJg")
    Observable<PlacesResponse> getPlaceIds(@Query("input") String input);
}
