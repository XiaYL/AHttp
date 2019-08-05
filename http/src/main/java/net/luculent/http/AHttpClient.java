package net.luculent.http;

import android.content.Context;

import net.luculent.http.converter.DataConverter;
import net.luculent.http.https.SSLCertificate;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;

/**
 * Created by xiaya on 2017/6/17
 */

public class AHttpClient {

    public static void init(IAHttpClient iAHttpClient) {
        AHttpWrapper.getInstance().init(iAHttpClient);
    }

    /**
     * 原先的单例没法动态配置参数，修改为每次实例化
     *
     * @return 客户端实例
     */
    public static SimpleSubscription get() {
        AHttpWrapper httpWrapper = AHttpWrapper.getInstance();
        return new SimpleSubscription(httpWrapper.getAHttpClient()).jsonParser(httpWrapper.getJsonParser());
    }

    private AHttpClient(Builder builder) {
        AHttpWrapper.getInstance().initOkHttpClient(builder);
    }

    public OkHttpClient getOkHttpClient() {
        return AHttpWrapper.getInstance().getOkHttpClient();
    }

    public String getBaseUrl() {
        return AHttpWrapper.getInstance().getBaseUrl();
    }

    public static class Builder {
        Context context;
        String baseUrl;//retrofi2.0以后必须以'/'结束，否则创建报错
        long timeOut = 30 * 1000;
        boolean isLog = true;
        BasicParamsInterceptor basicInterceptor;
        List<Interceptor> interceptors;
        String[] hosts;//域名地址
        List<SSLCertificate> certificates;//证书存放路径
        String mockUrl;//mock地址
        DataConverter.IJsonParser jsonParser;

        public Builder(Context context) {
            this.context = context;
            interceptors = new ArrayList<>();
        }

        public Builder baseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }

        public Builder mockUrl(String mockUrl) {
            this.mockUrl = mockUrl;
            return this;
        }

        public Builder timeOut(long timeOut) {
            this.timeOut = timeOut;
            return this;
        }

        public Builder addLog(boolean isLog) {
            this.isLog = isLog;
            return this;
        }

        public Builder basicInterceptor(BasicParamsInterceptor basicInterceptor) {
            this.basicInterceptor = basicInterceptor;
            return this;
        }

        public Builder addInterceptor(Interceptor interceptor) {
            interceptors.add(interceptor);
            return this;
        }

        @SuppressWarnings("unchecked")
        public Builder setSSL(String[] hosts, List<SSLCertificate> certificates) {
            this.hosts = hosts;
            this.certificates = certificates;
            return this;
        }

        public Builder jsonParser(DataConverter.IJsonParser jsonParser) {
            this.jsonParser = jsonParser;
            return this;
        }

        public AHttpClient build() {
            return new AHttpClient(this);
        }
    }
}
