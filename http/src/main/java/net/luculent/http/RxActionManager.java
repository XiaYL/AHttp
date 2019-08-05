package net.luculent.http;

import rx.Subscription;

/**
 * Created by Tamic on 2017-01-16.
 */

public interface RxActionManager<T> {

    void add(T tag, Subscription subscription);

    void remove(T tag);

    void cancel(T tag);

    void cancelAll();
}
