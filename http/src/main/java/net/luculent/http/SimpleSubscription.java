package net.luculent.http;

import net.luculent.http.converter.ConverterFactory;
import net.luculent.http.converter.Convertor;
import net.luculent.http.converter.DataConverter;
import net.luculent.http.subscriber.DownloadCallback;
import net.luculent.http.subscriber.DownloadSubscriber;
import net.luculent.http.subscriber.download.DownloadInterceptor;
import net.luculent.http.subscriber.download.ProgressListener;

import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by xiayanlei on 2017/8/2.
 */

public class SimpleSubscription implements ISubscription<ResponseBody> {

    Observable.Transformer transformer;
    OkHttpClient okHttpClient;
    String baseUrl;
    NativeApiService apiService;
    Observable<ResponseBody> observable;
    String url;
    DataConverter.IJsonParser jsonParser;

    SimpleSubscription(AHttpClient client) {
        this(client.getOkHttpClient(), client.getBaseUrl());
    }

    public SimpleSubscription(OkHttpClient okHttpClient, String baseUrl) {
        this.okHttpClient = okHttpClient;
        this.baseUrl = baseUrl;
        apiService = createApi(NativeApiService.class);
        transformer = new Observable.Transformer() {
            @Override
            public Object call(Object observable) {
                return ((Observable) observable).subscribeOn(Schedulers.io())
                        .unsubscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
            }
        };
        jsonParser = new DataConverter.DefaultJsonParser();
    }

    public SimpleSubscription jsonParser(DataConverter.IJsonParser jsonParser) {
        if (jsonParser != null) {
            this.jsonParser = jsonParser;
        }
        return this;
    }

    /**
     * 新建api
     *
     * @param api
     * @param <K>
     * @return
     */
    @Override
    public <K> K createApi(Class<K> api) {
        return createApi(api, okHttpClient, baseUrl);
    }

    @Override
    public <K> K createApi(Class<K> api, OkHttpClient okHttpClient, String baseUrl) {
        Retrofit.Builder builder = new Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(baseUrl)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create());
        return builder.build().create(api);
    }

    @Override
    public SimpleSubscription doGet(String url) {
        this.url = url;
        observable = compose(apiService.nativeGet(url));
        return this;
    }

    @Override
    public SimpleSubscription doGet(String url, RequestParams params) {
        this.url = url;
        observable = compose(apiService.nativeGet(url, params.bodyMap));
        return this;
    }

    @Override
    public SimpleSubscription doPost(String url, RequestParams params) {
        if (params.fileList.size() > 0) {
            return uploadFiles(url, params);
        }
        this.url = url;
        observable = compose(apiService.nativePost(url, params.bodyMap));
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public SimpleSubscription uploadFiles(String url, RequestParams params) {
        this.url = url;
        observable = compose(apiService.uploadMultipleFiles(url, params.convertBodyMap(), params.convertFileMap()));
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Subscription downloadFile(String url, String path, final DownloadCallback callback) {
        //文件下载需要单独监听下载进度
        ProgressListener listener = new ProgressListener() {
            @Override
            public void onProgress(long progress, long total, boolean done) {
                if (total > 0) {
                    callback.onProgress(progress, total);
                }
            }
        };
        OkHttpClient.Builder builder = okHttpClient.newBuilder();
        builder.addNetworkInterceptor(new DownloadInterceptor(listener));
        NativeApiService apiService = createApi(NativeApiService.class, builder.build(), baseUrl);
        return compose(apiService.downloadFile(url)).subscribe(new DownloadSubscriber(path, callback));
    }

    private <T> Observable compose(Observable<T> observable) {
        return observable.compose(transformer);
    }

    public Observable<ResponseBody> baseConverter() {
        return observable;
    }

    public <K> Convertor<K> clazzConverter(Class<K> clazz) {
        return makeFactory(clazz).clazzConverter();
    }

    public <K> Convertor<List<K>> listConverter(Class<K> clazz) {
        return makeFactory(clazz).listConverter();
    }

    private <K> ConverterFactory<K> makeFactory(Class<K> clazz) {
        return new ConverterFactory<>(new Convertor<>(observable, url), jsonParser, clazz);
    }
}
