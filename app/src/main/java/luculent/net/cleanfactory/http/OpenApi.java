package luculent.net.cleanfactory.http;

import net.luculent.http.SimpleSubscription;
import net.luculent.http.annotations.AParamMap;
import net.luculent.http.annotations.APath;

import java.util.Map;

/**
 * Created by xiayanlei on 2019/8/2.
 */

public interface OpenApi {

    @APath("todayVideo")
    SimpleSubscription todayVideo(@AParamMap Map<String, String> map);
}
