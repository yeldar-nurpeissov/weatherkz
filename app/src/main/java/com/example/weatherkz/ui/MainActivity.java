package com.example.weatherkz.ui;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.example.weatherkz.R;
import com.jakewharton.rxbinding2.widget.RxTextView;

import javax.inject.Inject;

import dagger.android.AndroidInjection;

public class MainActivity extends AppCompatActivity {

    @Inject
    ViewModelProvider.Factory factory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MainViewModel viewModel = ViewModelProviders.of(this, factory).get(MainViewModel.class);
        viewModel.setViewObserver(RxTextView.textChanges((findViewById(R.id.auto_complete_text_view))));
        viewModel.getResult().observe(this, list -> {
            ((TextView) findViewById(R.id.text)).setText(list.toString());
        });
    }
}
