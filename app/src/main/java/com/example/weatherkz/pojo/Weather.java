package com.example.weatherkz.pojo;

import android.arch.persistence.room.Entity;
import android.support.annotation.NonNull;

@Entity(primaryKeys = {"cityName", "temperature"})
public class Weather {
    private long queryId;
    @NonNull
    private String cityName;
    private float temperature;

    public Weather(long queryId, @NonNull String cityName, float temperature) {
        this.queryId = queryId;
        this.cityName = cityName;
        this.temperature = temperature;
    }

    public long getQueryId() {
        return queryId;
    }

    @NonNull
    public String getCityName() {
        return cityName;
    }

    public float getTemperature() {
        return temperature;
    }
}
