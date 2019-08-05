package net.luculent.http.subscriber;

import android.util.Log;

import rx.Subscriber;

/**
 * Created by xiaya on 2017/6/17.
 */

public class SimpleSubscriber<T> extends Subscriber<T> {
    private static final String TAG = "AHttpClient";

    @Override
    public void onCompleted() {

    }

    @Override
    public void onError(Throwable e) {
        Log.e(TAG, "onError: ", e);
    }

    @Override
    public void onNext(T t) {

    }
}
