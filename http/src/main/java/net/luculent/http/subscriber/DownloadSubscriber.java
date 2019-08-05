package net.luculent.http.subscriber;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.ResponseBody;

/**
 * Created by xiaya on 2017/6/17.
 */

public class DownloadSubscriber extends SimpleSubscriber<ResponseBody> {

    private static final int DOWNLOAD_SUCCESS = 1;
    private static final int DOWNLOAD_FAILED = 2;
    private String filepath;
    private DownloadCallback downloadCallback;

    public DownloadSubscriber(String path, DownloadCallback downloadCallback) {
        filepath = path;
        this.downloadCallback = downloadCallback;
    }

    private final Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if (downloadCallback == null) {
                return;
            }
            switch (msg.what) {
                case DOWNLOAD_SUCCESS:
                    downloadCallback.onSuccess(new File(filepath));
                    break;
                case DOWNLOAD_FAILED:
                    downloadCallback.onFail();
                    break;
            }
        }
    };

    @Override
    public void onError(Throwable e) {
        if (downloadCallback != null) {
            downloadCallback.onFail();
        }
    }

    @Override
    public void onNext(ResponseBody responseBody) {
        boolean result = writeResponseBodyToDisk(filepath, responseBody);
        handler.sendEmptyMessage(result ? DOWNLOAD_SUCCESS : DOWNLOAD_FAILED);
    }


    private boolean writeResponseBodyToDisk(String path, ResponseBody body) {
        try {
            File localFile = new File(path);
            if (!localFile.getParentFile().exists()) {
                localFile.getParentFile().mkdirs();
            }
            if (localFile.exists()) {
                localFile.delete();
            }
            InputStream inputStream = null;
            OutputStream outputStream = null;
            try {
                byte[] fileReader = new byte[4096];
                int read;
                inputStream = body.byteStream();
                outputStream = new FileOutputStream(localFile);
                while ((read = inputStream.read(fileReader)) != -1) {
                    outputStream.write(fileReader, 0, read);
                }
                outputStream.flush();
                return true;
            } catch (IOException e) {
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (Exception e) {
            return false;
        }
    }
}
