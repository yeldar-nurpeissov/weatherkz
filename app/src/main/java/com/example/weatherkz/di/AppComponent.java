package com.example.weatherkz.di;

import android.app.Application;

import com.example.weatherkz.WeatherApp;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjectionModule;

@Singleton
@Component(modules = {
        AndroidInjectionModule.class,
        ActivityModule.class,
        AppModule.class})
public interface AppComponent {
    void inject(WeatherApp app);

    @Component.Builder
    interface Builder {
        @BindsInstance
        Builder application(Application application);

        AppComponent build();
    }
}
