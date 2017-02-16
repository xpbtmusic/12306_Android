package com.akari.tickets.subscriber;

import rx.Observer;

/**
 * Created by Akari on 2017/2/16.
 */

public class BaseObserver<T> implements Observer<T> {
    @Override
    public void onCompleted() {

    }

    @Override
    public void onError(Throwable e) {
        e.printStackTrace();
    }

    @Override
    public void onNext(T t) {

    }
}
