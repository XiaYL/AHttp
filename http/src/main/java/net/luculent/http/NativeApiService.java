package net.luculent.http;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.QueryMap;
import retrofit2.http.Streaming;
import retrofit2.http.Url;
import rx.Observable;

/**
 * Created by xiaya on 2017/6/17.
 *
 * @Url 支持动态修改请求路径，规则：若注解的url包含一个完整的uri格式，则url将替换baseurl，否则url当作路径加在baseurl后面
 */

public interface NativeApiService {

    @GET
    Observable<ResponseBody> nativeGet(@Url String url);

    @GET
    Observable<ResponseBody> nativeGet(@Url String url, @QueryMap Map<String, String> maps);


    @FormUrlEncoded
    @POST
    Observable<ResponseBody> nativePost(@Url String url, @FieldMap Map<String, String> maps);

    @Multipart
    @POST
    Observable<ResponseBody> uploadMultipleFiles(@Url String url, @PartMap() Map<String, RequestBody> partMap, @Part
            List<MultipartBody.Part> files);

    @Streaming
    @GET
    Observable<ResponseBody> downloadFile(@Url String url);

}
