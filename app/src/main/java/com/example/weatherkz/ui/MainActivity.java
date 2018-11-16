package com.example.weatherkz.ui;

import android.os.Bundle;
import android.widget.TextView;

import com.example.weatherkz.R;
import com.jakewharton.rxbinding2.widget.RxTextView;

import javax.inject.Inject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(decor);

        viewModel.getResult().observe(this, list ->
                adapter.replace(list));

        viewModel.getLastQuery().observe(this, s ->
                ((TextView) findViewById(R.id.edit_text_city_name)).setText(s));
    }
}
