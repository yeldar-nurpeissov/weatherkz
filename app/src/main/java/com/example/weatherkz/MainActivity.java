package com.example.weatherkz;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Pair;
import android.widget.EditText;

import com.jakewharton.rxbinding2.widget.RxTextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import io.reactivex.Flowable;
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
                .map(CharSequence::toString)
                .flatMap(query -> {
                    QueryTime queryTime = getQuery(query);
                    return getFromRemote(query, queryTime);
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        disposable.add(obs.subscribe(string -> Log.d("tag", "debounced " + Arrays.toString(string.toArray()))));
        if (savedInstanceState == null) {
            (((EditText) findViewById(R.id.auto_complete_text_view))).setText(" It works");
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (disposable != null) {
            disposable.dispose();
        }
    }

    public Observable<List<AutoCompleteResult>> getACR(String query) {
        ArrayList<AutoCompleteResult> item = new ArrayList<>();
        item.add(new AutoCompleteResult());
        return Observable.just(item);
    }

    public Observable<List<String>> getFromRemote(String query, QueryTime queryTime) {
        AtomicBoolean isFresh = new AtomicBoolean(false);
        return Observable.create(emitter -> {
            if (queryTime != null)
                Flowable.just(new ArrayList<String>()).subscribe(s -> {
                    if (!isFresh.get()) {
                        emitter.onNext(s);
                    }
                });
            if (queryTime.shouldRefresh()) {
                Observable<List<AutoCompleteResult>> fetchedData = getACR(query);
                fetchedData.flatMapIterable(autoCompleteResults -> autoCompleteResults)
                        .flatMap(autoCompleteResult -> Observable.just(new Pair(1l, 1l)))
                        .flatMap(p -> Observable.just("S: fsdfsd","1","2"))
                        .reduce(new ArrayList<String>(), (s1, s2) -> {
                            s1.add(s2);
                            return s1;
                        })
                        .map(strings -> {/*insert room */
                            return strings;
                        }).subscribe(strings -> {
                    isFresh.set(true);
                    emitter.onNext(strings);
                });
            }
        });
    }

    public QueryTime getQuery(String query) {
        return new QueryTime(query, System.currentTimeMillis());
    }
}
