package com.example.weatherkz.pojo;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.support.annotation.NonNull;

@Entity(primaryKeys = {"cityName", "temperature"}, indices = {@Index(value = {"cityName", "temperature"}, unique = true)})
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

    public void setQueryId(long id) {
        this.queryId = id;
    }

    @NonNull
    public String getCityName() {
        return cityName;
    }

    public float getTemperature() {
        return temperature;
    }
}
