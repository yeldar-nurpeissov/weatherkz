package com.example.weatherkz;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Pair;
import android.widget.TextView;

import com.example.weatherkz.pojo.PlacesResponse;
import com.jakewharton.rxbinding2.widget.RxTextView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private CompositeDisposable disposable = new CompositeDisposable();
    private WeatherService weatherApiService;
    private GoogleApiService googleApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        OkHttpClient client = new OkHttpClient();
        googleApi = new Retrofit.Builder().client(client).baseUrl("https://maps.googleapis.com")

                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build().create(GoogleApiService.class);

        weatherApiService = new Retrofit.Builder().client(client).baseUrl("http://api.openweathermap.org")

                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build().create(WeatherService.class);

        Observable<List<String>> obs = RxTextView.textChanges((findViewById(R.id.auto_complete_text_view)))
                .filter(charSequence ->
                        charSequence.length() >= 2)
                .debounce(300, TimeUnit.MILLISECONDS)
                .map(CharSequence::toString)
                .flatMap(query -> {
                    QueryTime queryTime = getQuery(query);
                    return getFromRemote(query, queryTime);
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        disposable.add(obs.subscribe(string -> ((TextView) findViewById(R.id.text)).setText(string.toString())));
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (disposable != null) {
            disposable.dispose();
        }
    }

    public Observable<List<String>> getFromRemote(String query, QueryTime queryTime) {
        AtomicBoolean isFresh = new AtomicBoolean(false);
        return Observable.create(emitter -> {
            if (queryTime != null)
                Flowable.just(new ArrayList<String>()).subscribe(s -> {
                    if (!isFresh.get()) {
                        /* load data from db */
                        emitter.onNext(s);
                    }
                });
            if (queryTime == null || queryTime.shouldRefresh()) {
                googleApi.getPlaceIds(query)
                        .map(PlacesResponse::getPredictions)
                        .flatMapIterable(predictionList -> predictionList)
                        .flatMap(prediction ->
                                googleApi.getPlaceLocation(prediction.getPlaceId())
                                        .map(placeDetailResponse ->
                                                new Pair<>(placeDetailResponse, prediction.getStructuredFormatting().getMainText())))
                        .flatMap(placeDetailResponse -> {
                            Double lat = placeDetailResponse.first.getResult().getGeometry().getLocation().getLat();
                            Double lng = placeDetailResponse.first.getResult().getGeometry().getLocation().getLng();
                            return weatherApiService.getForecastByLocation(lat, lng)
                                    .map(weatherResponse -> new Pair<>(weatherResponse, placeDetailResponse.second));
                        })
                        .reduce(new ArrayList<String>(), (objects, weatherResponsePredictionPair) -> {
                            Double temp = weatherResponsePredictionPair.first.getMain().getTemp();
                            String city = weatherResponsePredictionPair.second;
                            objects.add(city + " :  " + temp);
                            return objects;
                        })
                        .map(strings -> {
                            /*insert into db */
                            return strings;
                        }).subscribeOn(Schedulers.io()).subscribe(strings -> {
                    isFresh.set(true);
                    emitter.onNext(strings);
                }, throwable -> throwable.printStackTrace());
            }
        });
    }

    public QueryTime getQuery(String query) {
        return new QueryTime(query, System.currentTimeMillis());
    }
}
