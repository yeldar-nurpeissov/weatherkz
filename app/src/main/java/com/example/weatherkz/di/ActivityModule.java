package com.example.weatherkz.di;

import com.example.weatherkz.ui.MainActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class ActivityModule {
    @ContributesAndroidInjector
    public abstract MainActivity mainActivity();
}
