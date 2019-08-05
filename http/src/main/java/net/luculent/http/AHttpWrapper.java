package net.luculent.http;

import net.luculent.http.converter.DataConverter;
import net.luculent.http.https.SSLManager;

import java.util.concurrent.TimeUnit;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * Created by xiayanlei on 2018/12/17.
 */
public class AHttpWrapper {

    private IAHttpClient iaHttpClient;

    private OkHttpClient okHttpClient;

    private String baseUrl;

    private String mockUrl;

    private DataConverter.IJsonParser jsonParser;

    private AHttpWrapper() {
    }

    void init(IAHttpClient iAHttpClient) {
        iaHttpClient = iAHttpClient;
        iaHttpClient.getAClient();
    }

    synchronized void initOkHttpClient(AHttpClient.Builder okBuilder) {
        setMockUrl(okBuilder.mockUrl);
        setJsonParser(okBuilder.jsonParser);
        if (getBaseUrl().equals(okBuilder.baseUrl)) {//服务器地址没有修改，不需要重新生成okhttp的实例
            if (okHttpClient == null) {
                okHttpClient = generateOkHttpClient(okBuilder);
            }
        } else {
            okHttpClient = generateOkHttpClient(okBuilder);
            baseUrl = okBuilder.baseUrl;
        }
    }

    public OkHttpClient generateOkHttpClient(AHttpClient.Builder okBuilder) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(okBuilder.timeOut, TimeUnit.MILLISECONDS)
                .readTimeout(okBuilder.timeOut, TimeUnit.MILLISECONDS)
                .writeTimeout(okBuilder.timeOut, TimeUnit.MILLISECONDS)
                .addNetworkInterceptor(new HttpLoggingInterceptor().setLevel(okBuilder.isLog ? HttpLoggingInterceptor
                        .Level.BODY : HttpLoggingInterceptor.Level.NONE));
        if (okBuilder.basicInterceptor != null) {
            builder.addInterceptor(okBuilder.basicInterceptor);
        }
        for (Interceptor interceptor : okBuilder.interceptors) {
            builder.addInterceptor(interceptor);
        }
        //https的请求才添加认证
        HttpUrl httpUrl = HttpUrl.parse(getBaseUrl());
        if (httpUrl != null && httpUrl.isHttps()) {
            if (okBuilder.certificates != null && okBuilder.certificates.size() > 0) {
                try {
                    SSLManager.SSLParams params = SSLManager.getSSLSocketFactory(okBuilder.context, okBuilder
                            .certificates);
                    builder.sslSocketFactory(params.sslSocketFactory, params.trustManager);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            builder.hostnameVerifier(SSLManager.getHostnameVerifier(okBuilder.hosts));
        }
        return builder.build();
    }

    AHttpClient getAHttpClient() {
        return iaHttpClient.getAClient();
    }

    OkHttpClient getOkHttpClient() {
        return okHttpClient;
    }

    String getBaseUrl() {
        return baseUrl == null ? "" : baseUrl;
    }

    void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getMockUrl() {
        return mockUrl == null ? "" : mockUrl;
    }

    public void setMockUrl(String mockUrl) {
        this.mockUrl = mockUrl;
    }

    public DataConverter.IJsonParser getJsonParser() {
        return jsonParser;
    }

    public void setJsonParser(DataConverter.IJsonParser jsonParser) {
        this.jsonParser = jsonParser;
    }

    public static AHttpWrapper getInstance() {
        return SingletonHolder.instance;
    }

    private static class SingletonHolder {
        private static AHttpWrapper instance = new AHttpWrapper();
    }
}
