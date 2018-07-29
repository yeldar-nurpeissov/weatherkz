package com.example.weatherkz.ui;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.example.weatherkz.R;
import com.jakewharton.rxbinding2.widget.RxTextView;

import javax.inject.Inject;

import dagger.android.AndroidInjection;

public class MainActivity extends AppCompatActivity {

    @Inject
    ViewModelProvider.Factory factory;
    private WeatherAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MainViewModel viewModel = ViewModelProviders.of(this, factory).get(MainViewModel.class);
        viewModel.setViewObserver(RxTextView.textChanges((findViewById(R.id.edit_text_city_name))));

        DividerItemDecoration decor = new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL);
        decor.setDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.item_divider));

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        adapter = new WeatherAdapter();

        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(decor);

        viewModel.getResult().observe(this, list ->
                adapter.replace(list));

        viewModel.getLastQuery().observe(this, s ->
                ((TextView) findViewById(R.id.edit_text_city_name)).setText(s));
    }
}
