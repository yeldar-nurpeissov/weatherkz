package com.example.weatherkz.db;

import com.example.weatherkz.pojo.QueryAndTime;
import com.example.weatherkz.pojo.Weather;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {QueryAndTime.class, Weather.class}, version = 1)
public abstract class WeatherDatabase extends RoomDatabase {
    public abstract WeatherDao weatherDao();
}