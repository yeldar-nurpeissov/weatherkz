package com.example.weatherkz.repository;

import android.support.annotation.Nullable;
import android.util.Pair;

import com.example.weatherkz.api.GoogleApiService;
import com.example.weatherkz.api.WeatherService;
import com.example.weatherkz.db.WeatherDao;
import com.example.weatherkz.db.WeatherDatabase;
import com.example.weatherkz.pojo.QueryAndTime;
import com.example.weatherkz.pojo.Weather;
import com.example.weatherkz.pojo.response.PlacesResponse;
import com.example.weatherkz.pojo.response.WeatherResponse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.inject.Inject;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

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

    public Maybe<String> getLastQuery() {
        return weatherDao.getLastQuery()
                .subscribeOn(Schedulers.io());
    }

    public Observable<Pair<String, QueryAndTime>> getQueryTime(String query) {
        return weatherDao.getQueryAndTime(query)
                .subscribeOn(Schedulers.io())
                .onErrorResumeNext(observer -> {
                    QueryAndTime o = new QueryAndTime(0, query, 0);
                    return Single.just(o);
                }).toObservable()
                .map(queryAndTime -> new Pair<>(query, queryAndTime));
    }

    @Nullable
    public ObservableSource<? extends List<Weather>> getWeather(Pair<String, QueryAndTime> pair) {
        Observable<List<Weather>> weather = null;
        if (pair.second.getId() != 0) {
            weather = weatherDao.getWeathers(pair.second.getId()).toObservable();
        }
        if (pair.second.shouldRefresh()) {
            Observable<List<Weather>> fromRemote = getFromRemote(pair.first);
            if (weather == null) {
                return fromRemote;
            } else {
                return Observable.concat(weather, fromRemote);
            }
        }

        return weather;
    }

    private Observable<List<Weather>> getFromRemote(String query) {
        List<Weather> cWeather = new ArrayList<>();
        return getPlaceNames(query)
                .map(PlacesResponse::getPredictions)
                .flatMapIterable(cityList -> cityList)
                .flatMap(city -> getWeatherByCityNameAndCounty(city.getStructuredFormatting().getMainText()))
                .filter(weatherResponse -> weatherResponse.getCod() == 200)
                .collect(() -> cWeather, (weathers, weatherResponse) ->
                        weathers.add(new Weather(0, weatherResponse.getCityName(), weatherResponse.getMain().getTemp()))).toObservable()
                .flatMap(weathers -> Observable.fromCallable(() -> insertData(query, weathers)));
    }

    private Observable<PlacesResponse> getPlaceNames(String query) {
        return googleApiService.getPlaceNames(query)
                .subscribeOn(Schedulers.io())
                .onErrorResumeNext(next -> {
                    PlacesResponse item1 = new PlacesResponse();
                    item1.setStatus("404");
                    Observable.just(item1);
                }).filter(placesResponse -> placesResponse.getStatus().equalsIgnoreCase("ok"));
    }

    private Observable<WeatherResponse> getWeatherByCityNameAndCounty(String cityName) {
        return weatherService.getWeatherByCityNameAndCounty(String.format("%s,kz", cityName))
                .subscribeOn(Schedulers.io())
                .onErrorResumeNext(observer -> {
                    WeatherResponse item = new WeatherResponse();
                    item.setCod(404);
                    return Observable.just(item);
                });
    }

    private List<Weather> insertData(String query, List<Weather> weathers) {
        AtomicBoolean successfullyInserted = new AtomicBoolean(false);
        database.beginTransaction();
        try {
            QueryAndTime queryAndTime = new QueryAndTime(0, query, System.currentTimeMillis());
            long id = weatherDao.insert(queryAndTime);
            for (Weather weather : weathers) {
                weather.setQueryId(id);
            }
            weatherDao.insert(weathers);
            database.setTransactionSuccessful();
            successfullyInserted.set(true);
        } finally {
            database.endTransaction();
        }

        return successfullyInserted.get() ? weathers : Collections.emptyList();
    }
}
