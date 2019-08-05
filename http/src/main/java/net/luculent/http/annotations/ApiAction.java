package net.luculent.http.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by XIAYANLEI on 2017/9/29.
 * 用于区别请求方法
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ApiAction {

    Action value() default Action.COMMON;

    enum Action {
        COMMON() {//普通post请求

        }, DOWNLOAD() {//文件下载

        }, UPLOAD() {//文件上传

        };
    }
}
