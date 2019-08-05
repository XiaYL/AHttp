package net.luculent.http.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by XIAYANLEI on 2017/9/29.
 * 请求参数注解
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface AParam {

    String value();

    boolean sort() default false;//参数为空的时候，是否过滤，true则不添加到请求参数，false-转换成""添加到请求参数
}
