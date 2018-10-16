package com.example.franc.beginnerrx;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class DemoModelActivity extends AppCompatActivity {
    private static final String TAG = DemoModelActivity.class.getSimpleName();
    CompositeDisposable compositeDisposable;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo_model);
        compositeDisposable =new CompositeDisposable();

        compositeDisposable.add(getNotesObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Function<Model,Model>(){

                    @Override
                    public Model apply(Model model) throws Exception {
                        model.setNote(model.getNote().toUpperCase());
                        return model;
                    }
                })
                .subscribeWith(getNotesObserver()));
    }
    private DisposableObserver<Model> getNotesObserver() {
        return new DisposableObserver<Model>() {

            @Override
            public void onNext(Model note) {
                Log.d(TAG, "Note: " + note.getNote());
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError: " + e.getMessage());
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "All notes are emitted!");
            }
        };
    }
    private Observable<Model> getNotesObservable() {
        final List<Model> list = models();
        return Observable.create(new ObservableOnSubscribe<Model>() {
            @Override
            public void subscribe(ObservableEmitter<Model> emitter) throws Exception {
                for(Model model : list){
                    if(!emitter.isDisposed()){
                        emitter.onNext(model);
                    }
                }
                if (!emitter.isDisposed()){
                    emitter.onComplete();
                }
            }
        });
    }

    public List<Model> models (){
        List<Model> list = new ArrayList<>();
        list.add(new Model(1,"Nghia"));
        list.add(new Model(2,"Nhi"));
        list.add(new Model(3,"Hang"));
        list.add(new Model(4,"Lan"));
        list.add(new Model(5,"Phuong"));
        return list;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }
}
