package net.luculent.http.converter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import rx.functions.Func1;

/**
 * Created by xiayanlei on 2019/4/1.
 */

public class ConverterFactory<T> {

    private Convertor<ResponseBody> convertor;
    private DataConverter.IJsonParser jsonParser;
    private Class<T> clazz;

    public ConverterFactory(Convertor<ResponseBody> convertor, DataConverter.IJsonParser jsonParser, Class<T> clazz) {
        this.convertor = convertor;
        this.jsonParser = jsonParser;
        this.clazz = clazz;
    }

    public Convertor<ResponseBody> convert() {
        return convertor;
    }

    public Convertor<T> clazzConverter() {
        return convertor.convert(new Func1<ResponseBody, T>() {
            @Override
            public T call(ResponseBody body) {
                return new Func1Impl<>(clazz).parseObject(body);
            }
        });
    }

    public Convertor<List<T>> listConverter() {
        return convertor.convert(new Func1<ResponseBody, List<T>>() {
            @Override
            public List<T> call(ResponseBody body) {
                return new Func1Impl<>(clazz).parseArray(body);
            }
        });
    }

    private class Func1Impl<T> {

        private Class<T> clazz;

        public Func1Impl(Class<T> clazz) {
            this.clazz = clazz;
        }

        public T parseObject(ResponseBody body) {
            try {
                String result = body.string();
                if (clazz == String.class) {
                    return (T) result;
                }
                if (clazz == JSONObject.class) {
                    return (T) new JSONObject(result);
                }
                return jsonParser.parseObject(result, clazz);
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        public List<T> parseArray(ResponseBody body) {
            String result = null;
            try {
                result = body.string();
            } catch (IOException e) {
                e.printStackTrace();
                return new ArrayList<>();
            }
            return parseArray(result);
        }

        public List<T> parseArray(String result) {
            return jsonParser.parseArray(result, clazz);
        }
    }
}
