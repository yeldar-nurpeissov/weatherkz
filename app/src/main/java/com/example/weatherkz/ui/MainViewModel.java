package com.example.weatherkz.ui;

import com.example.weatherkz.pojo.Weather;
import com.example.weatherkz.repository.WeatherRepository;
import com.jakewharton.rxbinding2.InitialValueObservable;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class MainViewModel extends ViewModel {
    private final WeatherRepository weatherRepository;
    private final MutableLiveData<List<Weather>> result = new MutableLiveData<>();
    private final MutableLiveData<String> lastQuery = new MutableLiveData<>();
    private final CompositeDisposable disposable;

    @Inject
    public MainViewModel(WeatherRepository weatherRepository) {
        this.weatherRepository = weatherRepository;
        disposable = new CompositeDisposable();
        disposable.add(weatherRepository.getLastQuery().subscribe(lastQuery::postValue));
    }

    public LiveData<List<Weather>> getResult() {
        return result;
    }

    public LiveData<String> getLastQuery() {
        return lastQuery;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        disposable.clear();
    }

    public void setViewObserver(InitialValueObservable<CharSequence> viewObserver) {
        disposable.add(viewObserver
                .filter(input -> input.length() >= 2)
                .debounce(300, TimeUnit.MILLISECONDS)
                .map(CharSequence::toString)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .switchMap(weatherRepository::getQueryTime)
                .switchMap(weatherRepository::getWeather)
                .subscribe(result::postValue));
    }
}

