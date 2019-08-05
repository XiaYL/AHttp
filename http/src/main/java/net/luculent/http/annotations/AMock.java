package net.luculent.http.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by xiayanlei on 2019/3/12.
 * mock模拟数据
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AMock {

    String server() default "";//格式同baseUrl，以“/”结束，否则server无效

    boolean useMock() default true;//true-使用mock数据，false-不请求mock地址
}
