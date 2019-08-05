package net.luculent.http;

import net.luculent.http.subscriber.DownloadCallback;

import okhttp3.OkHttpClient;
import rx.Subscription;

/**
 * Created by xiayanlei on 2017/8/2.
 */

public interface ISubscription<T> {

    ISubscription<T> doGet(String url);

    ISubscription<T> doGet(String url, RequestParams params);

    ISubscription<T> doPost(String url, RequestParams params);

    ISubscription<T> uploadFiles(String url, RequestParams params);

    Subscription downloadFile(String url, String path, DownloadCallback callback);

    <K> K createApi(Class<K> api);

    <K> K createApi(Class<K> api, OkHttpClient okHttpClient, String baseUrl);
}
