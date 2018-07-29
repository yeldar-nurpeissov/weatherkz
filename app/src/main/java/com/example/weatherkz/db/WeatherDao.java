package com.example.weatherkz.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;

import com.example.weatherkz.pojo.QueryAndTime;
import com.example.weatherkz.pojo.Weather;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Single;

@Dao
public abstract class WeatherDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract long insert(QueryAndTime queryAndTime);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insert(Weather weather);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insert(List<Weather> weathers);

    @Query("SELECT * FROM queryandtime WHERE `query` = :query")
    public abstract Single<QueryAndTime> getQueryAndTime(String query);

    @Query("SELECT * FROM weather WHERE queryId =:queryId")
    public abstract Flowable<List<Weather>> getWeathers(long queryId);

    @Delete
    public abstract int deleteQueryAndTime(QueryAndTime queryAndTime);

    @Query("DELETE FROM weather WHERE queryId =:queryId")
    public abstract int deleteWeather(long queryId);

    @Transaction
    public void deleteQueryAndWeather(String query) {

    }

    @Query("SELECT `query` FROM queryandtime ORDER BY time DESC LIMIT 1")
    public abstract Maybe<String> getLastQuery();
}
