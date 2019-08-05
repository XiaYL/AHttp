package luculent.net.cleanfactory.http;

import android.content.Context;

import com.alibaba.fastjson.JSON;

import net.luculent.http.AHttpClient;
import net.luculent.http.IAHttpClient;
import net.luculent.http.converter.DataConverter;

import java.util.List;

/**
 * Created by xiayanlei on 2019/8/2.
 */

public class AHttpClientImpl implements IAHttpClient {

    private Context context;

    public AHttpClientImpl(Context context) {
        this.context = context;
    }

    @Override
    public AHttpClient getAClient() {
        return new AHttpClient.Builder(context)
                .baseUrl("https://api.apiopen.top/")
                .jsonParser(new FastJSONParser())
                .timeOut(30 * 1000)
                .build();
    }

    class FastJSONParser implements DataConverter.IJsonParser {

        @Override
        public <T> T parseObject(String text, Class<T> clazz) {
            return JSON.parseObject(text, clazz);
        }

        @Override
        public <T> List<T> parseArray(String text, Class<T> clazz) {
            return JSON.parseArray(text, clazz);
        }
    }
}
