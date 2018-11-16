package com.example.weatherkz.di;

import android.app.Application;

import com.example.weatherkz.db.WeatherDao;
import com.example.weatherkz.db.WeatherDatabase;

import javax.inject.Singleton;

import androidx.room.Room;
import dagger.Module;
import dagger.Provides;

@Module(includes = ApiModule.class)
public class AppModule {

    @Singleton
    @Provides
    WeatherDatabase weatherDB(Application application) {
        return Room.databaseBuilder(application, WeatherDatabase.class, "weather_kz.db").build();
    }

    @Singleton
    @Provides
    WeatherDao weatherDao(WeatherDatabase database) {
        return database.weatherDao();
    }
}
