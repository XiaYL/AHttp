package net.luculent.http.subscriber;

import java.io.File;

import okhttp3.ResponseBody;

/**
 * Created by xiaya on 2017/6/17.
 */

public interface DownloadCallback {

    void onProgress(long download, long total);

    void onSuccess(File file);

    void onFail();
}
