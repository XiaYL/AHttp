package luculent.net.cleanfactory;

import android.app.Application;

import net.luculent.http.AHttpClient;

import luculent.net.cleanfactory.http.AHttpClientImpl;

/**
 * Created by xiayanlei on 2019/8/2.
 */

public class CustomApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        AHttpClient.init(new AHttpClientImpl(this));
    }
}
