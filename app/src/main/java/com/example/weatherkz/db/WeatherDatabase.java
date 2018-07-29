package com.example.weatherkz.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.example.weatherkz.pojo.QueryAndTime;
import com.example.weatherkz.pojo.Weather;

@Database(entities = {QueryAndTime.class, Weather.class}, version = 1)
public abstract class WeatherDatabase extends RoomDatabase {
    public abstract WeatherDao weatherDao();
}
