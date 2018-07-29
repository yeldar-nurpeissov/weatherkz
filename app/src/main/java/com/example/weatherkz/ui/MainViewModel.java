package com.example.weatherkz.ui;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.example.weatherkz.repository.WeatherRepository;
import com.example.weatherkz.api.GoogleApiService;
import com.example.weatherkz.api.WeatherService;
import com.example.weatherkz.pojo.QueryAndTime;
import com.example.weatherkz.pojo.response.PlacesResponse;
import com.example.weatherkz.pojo.response.WeatherResponse;
import com.jakewharton.rxbinding2.InitialValueObservable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.inject.Inject;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class MainViewModel extends ViewModel {
    private final WeatherRepository weatherRepository;
    private MutableLiveData<List<String>> result = new MutableLiveData<>();
    private CompositeDisposable disposable;

    @Inject
    public MainViewModel(WeatherRepository weatherRepository) {
        this.weatherRepository = weatherRepository;
        disposable = new CompositeDisposable();
    }


    public LiveData<List<String>> getResult() {
        return result;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        disposable.clear();
    }

    public void setViewObserver(InitialValueObservable<CharSequence> viewObserver) {
        Observable<List<String>> listObservable = viewObserver
                .filter(input -> input.length() >= 2)
                .debounce(300, TimeUnit.MILLISECONDS)
                .map(CharSequence::toString)
                .flatMap(query -> {
                    QueryAndTime queryAndTime = getQueryTime(query);
                    return getFromRemote(query, queryAndTime);
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        disposable.add(listObservable.subscribe(string -> {
            result.setValue(string);
        }));
    }

    private Observable<List<String>> getFromRemote(String query, QueryAndTime queryAndTime) {
        AtomicBoolean isFresh = new AtomicBoolean(false);
        return Observable.create(emitter -> {
            if (queryAndTime != null)
                Flowable.just(new ArrayList<String>()).subscribe(s -> {
                    if (!isFresh.get()) {
                        /* load data from db */
                        emitter.onNext(s);
                    }
                });
            if (queryAndTime == null || queryAndTime.shouldRefresh()) {
                weatherRepository.getPlaceIds(query)
                        .map(PlacesResponse::getPredictions)
                        .flatMapIterable(cityList -> cityList)
                        .flatMap(city -> {
                            String cityName = city.getStructuredFormatting().getMainText();
                            return weatherRepository.getForecastByCityNameAndCounty(String.format("%s,kz", cityName));
                        })
                        .onErrorResumeNext(observer -> {
                            WeatherResponse item = new WeatherResponse();
                            item.setCod(404);
                            return Observable.just(item);
                        })
                        .filter(weatherResponse -> weatherResponse.getCod() == 200)
                        .collect((Callable<ArrayList<String>>) ArrayList::new, (list, weatherResponse) ->
                                list.add(weatherResponse.getCityName() + " " + weatherResponse.getMain().getTemp()))
                        .map(strings -> {
                            /*insert into db */
                            return strings;
                        }).subscribeOn(Schedulers.io()).subscribe(strings -> {
                    isFresh.set(true);
                    emitter.onNext(strings);
                }, Throwable::printStackTrace);
            }
        });
    }

    private QueryAndTime getQueryTime(String query) {
        return new QueryAndTime(0, query, System.currentTimeMillis());
    }
}
