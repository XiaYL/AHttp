package net.luculent.http;

import android.os.Environment;
import android.util.Log;

import net.luculent.http.annotations.ACall;
import net.luculent.http.annotations.AFile;
import net.luculent.http.annotations.AFileMap;
import net.luculent.http.annotations.AFilePath;
import net.luculent.http.annotations.AMock;
import net.luculent.http.annotations.AParam;
import net.luculent.http.annotations.AParamMap;
import net.luculent.http.annotations.APath;
import net.luculent.http.annotations.ApiAction;
import net.luculent.http.subscriber.DownloadCallback;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.Map;

import okhttp3.HttpUrl;
import rx.Subscription;

/**
 * Created by XIAYANLEI on 2017/9/29.
 * retrofit请求代理实现类
 */

public class ApiProxy {
    private static final String TAG = "ApiProxy";
    static final Map<Method, ApiMethod> apiCache = new LinkedHashMap<>();

    private ApiProxy() {
    }

    public static <T> T newProxyInstance(final Class<T> api) {

        return (T) Proxy.newProxyInstance(api.getClassLoader(), new Class[]{api}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                ApiMethod apiMethod = loadApiMethod(method);
                return apiMethod.call(args);
            }
        });
    }

    static ApiMethod loadApiMethod(Method method) {
        synchronized (apiCache) {
            ApiMethod apiMethod = apiCache.get(method);
            if (apiMethod == null) {
                Log.i(TAG, "loadApiMethod: register method = " + method.getName());
                apiMethod = new ApiMethod(method);
                apiCache.put(method, apiMethod);
            }
            return apiMethod;
        }
    }

    static class ApiMethod {
        ApiAction.Action action;
        String path;
        Annotation[][] parameterAnnotations;
        Type type;
        Builder builder;

        ApiMethod(Method method) {
            type = method.getGenericReturnType();
            ApiAction apiAction = method.getAnnotation(ApiAction.class);
            action = apiAction == null ? ApiAction.Action.COMMON : apiAction.value();
            APath aPath = method.getAnnotation(APath.class);
            path = aPath == null ? "" : aPath.value();
            checkMockServer(method);
            parameterAnnotations = method.getParameterAnnotations();
            if (action == ApiAction.Action.DOWNLOAD) {
                if (type != Subscription.class) {
                    throw new IllegalArgumentException("Service methods must return subscription");
                }
            } else if (type != SimpleSubscription.class) {
                throw new IllegalArgumentException("Service methods must return SimpleSubscription");
            }
        }

        /**
         * 2019-03-12，添加mock支持
         *
         * @param method
         */
        private void checkMockServer(Method method) {
            String mockUrl = AHttpWrapper.getInstance().getMockUrl();//全局mock地址
            if (mockUrl == null || mockUrl.trim().length() == 0) {//只有在设置了全局mock地址的情况下，单个api的mock才会有效
                return;
            }
            AMock aMock = method.getAnnotation(AMock.class);
            if (aMock != null && aMock.useMock()) {
                String mockServer = aMock.server().trim().length() > 0 ? aMock.server() : mockUrl;
                HttpUrl httpUrl = HttpUrl.parse(mockServer);
                if (httpUrl != null) {
                    path = mockServer.concat(path);
                }
            }
        }

        Object call(Object[] args) {
            builder = new Builder(action).url(path);
            for (int i = 0; i < parameterAnnotations.length; i++) {
                Object object = args[i];
                for (int j = 0; j < parameterAnnotations[i].length; j++) {
                    Annotation annotation = parameterAnnotations[i][j];
                    parseAnnotation(annotation, object);
                }
            }
            return builder.build();
        }

        /**
         * 解析参数
         *
         * @param annotation
         * @param object
         */
        private void parseAnnotation(Annotation annotation, Object object) {
            if (annotation instanceof APath) {//重定向请求地址
                builder.url(object == null ? null : object.toString());
            } else if (annotation instanceof AParam) {//请求参数
                if (object == null && ((AParam) annotation).sort()) {//如果入参为空，且需要过滤的时候，不加到请求参数

                } else {
                    builder.param(((AParam) annotation).value(), object == null ? "" : object.toString());
                }
            } else if (annotation instanceof AParamMap && object != null && object instanceof Map) {//多个请求参数
                builder.params((Map<String, String>) object);
            } else if (annotation instanceof AFile && object instanceof File) {//单个文件
                builder.file("file", (File) object);
            } else if (annotation instanceof AFileMap && object instanceof Map) {//多个文件，或者文件的key需要定制
                builder.files((Map<String, File>) object);
            } else if (annotation instanceof AFilePath) {//文件下载存放路径
                builder.filePath(object == null ? Environment.getExternalStorageDirectory() + "/" : object.toString());
            } else if (annotation instanceof ACall && object instanceof DownloadCallback) {//文件下载回调
                builder.downloadCall((DownloadCallback) object);
            }
        }
    }

    public static class Builder {
        private RequestParams params = new RequestParams();
        private String filePath;//文件下载路径
        private String url;//请求地址
        private DownloadCallback callback;//文件下载回调
        private ApiAction.Action action;

        public Builder() {
            this(ApiAction.Action.COMMON);
        }

        public Builder(ApiAction.Action action) {
            this.action = action;
        }

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Builder param(String key, String value) {
            params.addBodyParameter(key, value);
            return this;
        }

        public Builder params(Map<String, String> map) {
            params.addParams(map);
            return this;
        }

        public Builder filePath(String filePath) {
            this.filePath = filePath;
            return this;
        }

        public Builder file(String key, File file) {
            params.addBodyParameter(key, file);
            return this;
        }

        public Builder files(Map<String, File> files) {
            params.addFiles(files);
            return this;
        }

        Builder downloadCall(DownloadCallback callback) {
            this.callback = callback;
            return this;
        }

        Object build() {
            if (action == ApiAction.Action.DOWNLOAD) {
                return download(callback);
            } else {
                return call();
            }
        }

        public SimpleSubscription call() {
            if (action == ApiAction.Action.UPLOAD) {
                return AHttpClient.get().uploadFiles(url, params);
            } else {
                return AHttpClient.get().doPost(url, params);
            }
        }

        public Subscription download(DownloadCallback callback) {
            return AHttpClient.get().downloadFile(url, filePath, callback);
        }
    }
}
