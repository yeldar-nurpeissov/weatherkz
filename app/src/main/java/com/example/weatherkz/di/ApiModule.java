package com.example.weatherkz.di;

import com.example.weatherkz.api.GoogleApiService;
import com.example.weatherkz.api.WeatherService;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@Module(includes = ViewModelModule.class)
public class ApiModule {

    @Singleton
    @Provides
    HttpLoggingInterceptor httpLoggingInterceptor() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        return loggingInterceptor;
    }

    @Singleton
    @Provides
    OkHttpClient okHttpClient(HttpLoggingInterceptor loggingInterceptor) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.addInterceptor(loggingInterceptor);

        return builder.build();
    }

    @Singleton
    @Provides
    Retrofit.Builder retrofitBuilder(OkHttpClient client) {
        return new Retrofit.Builder()
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create());
    }

    @Singleton
    @Provides
    GoogleApiService googleApiService(Retrofit.Builder builder) {
        return builder.baseUrl("https://maps.googleapis.com").build().create(GoogleApiService.class);
    }

    @Singleton
    @Provides
    WeatherService weatherService(Retrofit.Builder builder) {
        return builder.baseUrl("http://api.openweathermap.org").build().create(WeatherService.class);
    }
}
