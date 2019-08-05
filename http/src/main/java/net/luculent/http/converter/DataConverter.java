package net.luculent.http.converter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiayanlei on 2018/4/1.
 */

public interface DataConverter {

    interface IJsonParser {

        <T> T parseObject(String text, Class<T> clazz);

        <T> List<T> parseArray(String text, Class<T> clazz);
    }

    class DefaultJsonParser implements IJsonParser {

        @Override
        public <T> T parseObject(String text, Class<T> clazz) {
            try {
                return clazz.newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
                return null;
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        public <T> List<T> parseArray(String text, Class<T> clazz) {
            return new ArrayList<>();
        }
    }
}
