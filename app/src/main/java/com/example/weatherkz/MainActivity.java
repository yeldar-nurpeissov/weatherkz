package com.example.weatherkz;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.jakewharton.rxbinding2.widget.RxTextView;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private CompositeDisposable disposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Observable<List<String>> obs = RxTextView.textChanges((findViewById(R.id.auto_complete_text_view)))
                .filter(charSequence ->
                        charSequence.length() >= 2)
                .debounce(300, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .map(CharSequence::toString)
                .flatMap(this::getQuery)
                .flatMap(queryTime -> getData(queryTime.shouldRefresh()))
                .observeOn(AndroidSchedulers.mainThread());

        disposable.add(obs.subscribe(string -> Log.d("tag", "debounced " + Arrays.toString(string.toArray()))));

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (disposable != null) {
            disposable.dispose();
        }
    }

    public Observable<List<String>> getData(boolean shouldFetch) {
        return shouldFetch ? getFromRemote() : getFromLocal();
    }

    public Observable<List<String>> getFromLocal() {
        return Observable.create(emitter -> {
            Thread.sleep(5000);
            emitter.onNext(Arrays.asList("1", "2"));
        });
    }

    public Observable<List<String>> getFromRemote() {
        return Observable.create(emitter -> emitter.onNext(Arrays.asList("3", "4")));
    }

    public Observable<QueryTime> getQuery(String query) {
        return Observable.just(new QueryTime(query, System.currentTimeMillis()));
    }
}
