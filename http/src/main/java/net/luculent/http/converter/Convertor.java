package net.luculent.http.converter;

import android.util.Log;

import net.luculent.http.RxApiManager;
import net.luculent.http.subscriber.SimpleSubscriber;

import rx.Observable;
import rx.Subscription;
import rx.functions.Func1;

/**
 * Created by xiayanlei on 2018/3/22.
 * Observable转换
 */

public class Convertor<K> {

    private Observable<K> observable;

    private Object object;

    private String url;

    public Convertor(Observable<K> observable, String url) {
        this.observable = observable;
        this.url = url;
    }

    public Convertor<K> inject(Object object) {
        this.object = object;
        return this;
    }

    public <T> Convertor<T> convert(Func1<K, T> func1) {
        Observable<T> tob = observable.map(func1);
        return new Convertor<>(tob, url);
    }

    public Observable<K> asObservable() {
        return observable;
    }

    public Observable<K> asSafeObservable(final K k) {
        return observable.onErrorReturn(new Func1<Throwable, K>() {
            @Override
            public K call(Throwable throwable) {
                return k;
            }
        });
    }

    public Subscription subscribe(SimpleSubscriber<K> subscriber) {
        if (subscriber == null) {
            subscriber = new SimpleSubscriber<>();
        }
        Subscription subscription = observable.subscribe(new WrapSimpleSubscriber<>(subscriber));
        if (object != null) {
            RxApiManager.get().add(getTag(), subscription);
        }
        return subscription;
    }

    private String getTag() {
        return object.getClass().getName() + "$$" + url;
    }

    class WrapSimpleSubscriber<T> extends SimpleSubscriber<T> {

        SimpleSubscriber<T> subscriber;

        public WrapSimpleSubscriber(SimpleSubscriber<T> subscriber) {
            this.subscriber = subscriber;
        }

        @Override
        public void onCompleted() {
            subscriber.onCompleted();
        }

        @Override
        public void onNext(T t) {
            try {
                subscriber.onNext(t);//防止在数据解析的时候出现报错
            } catch (Exception e) {
                Log.e("AHttpClient", "onNext: ", e);
            }
        }

        @Override
        public void onError(Throwable e) {
            if (!NullPointerException.class.isInstance(e)) {//忽略response为null的情况
                subscriber.onError(e);
            }
        }
    }
}
