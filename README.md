# AHttp
基于Retrofit封装的网络请求，支持https证书，上传、下载等通用功能。

step1：初始化，调用AHttpClient.init(IAHttpClient iAHttpClient)初始化配置，IAHttpClient是AHttpClient的实例化接口

    @Override
    public AHttpClient getAClient() {
        return new AHttpClient.Builder(context)
                .baseUrl("https://api.apiopen.top/") //服务器地址，必须以 "/"结束
                .addLog(false) //false-不使用logging-interceptor-logging
                .addInterceptor(new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
                    @Override
                    public void log(String s) {
                        Log.i(TAG, "log: " + s);
                    }
                }).setLevel(HttpLoggingInterceptor.Level.BODY))
                .mockUrl("") //mock测试地址
                .setSSL(null, null) // https证书配置
                .jsonParser(new FastJSONParser()) // 数据解析器
                .timeOut(30 * 1000)
                .build();
    }
    
step2：生成接口方法，类似retrofit的接口生成，可参照如下例子，目前仅支持返回SimpleSubscription

    public interface OpenApi {
        @APath("todayVideo")
        SimpleSubscription todayVideo(@AParamMap Map<String, String> map);
    }

可用的注解：
APath：请求路径注解
AMock：mock地址
AParam，AParamMap：请求参数
AFile，AFileMap：上传附件参数
AFilePath：文件下载存放路径

step3：根据接口访问服务

    ApiProxy.newProxyInstance(OpenApi.class)
                        .todayVideo(null)
                        .clazzConverter(TodayVideoBean.class) //将请求结果直接转换成实体类，需要配置IJsonParser
                        .subscribe(new SimpleSubscriber<TodayVideoBean>(){
                            @Override
                            public void onNext(TodayVideoBean todayVideoBean) {
                                resultTxt.setText(todayVideoBean.getMessage());
                            }
                        })
                ;
  
