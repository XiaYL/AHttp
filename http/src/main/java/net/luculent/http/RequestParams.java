package net.luculent.http;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Created by xiaya on 2017/6/17.
 */

public class RequestParams {

    Map<String, String> bodyMap = new HashMap<>();

    List<FileBody> fileList = new ArrayList<>();

    public void addBodyParameter(String key, String value) {
        bodyMap.put(key, value == null ? "" : value);//refrofit不允许value为空
    }

    public void addBodyParameter(String key, File file) {
        fileList.add(new FileBody(key, file));
    }

    public void addFiles(Map<String, File> map) {
        for (Map.Entry<String, File> entry : map.entrySet()) {
            addBodyParameter(entry.getKey(), entry.getValue());
        }
    }

    public void addParams(Map<String, String> map) {
        for (Map.Entry<String, String> entry : map.entrySet()) {
            addBodyParameter(entry.getKey(), entry.getValue());
        }
    }

    protected HashMap<String, RequestBody> convertBodyMap() {
        HashMap<String, RequestBody> map = new HashMap<>();
        for (Map.Entry<String, String> entry : bodyMap.entrySet()) {
            map.put(entry.getKey(), createPartFromString(entry.getValue()));
        }
        return map;
    }

    protected List<MultipartBody.Part> convertFileMap() {
        List<MultipartBody.Part> files = new ArrayList<>();
        for (FileBody fileBody : fileList) {
            files.add(prepareFilePart(fileBody.getName(), fileBody.getFile()));
        }
        return files;
    }

    private MultipartBody.Part prepareFilePart(String key, File file) {
        return MultipartBody.Part.createFormData(key, file.getName(), createFileBody(file));
    }

    private RequestBody createFileBody(File file) {
        return RequestBody.create(MediaType.parse("application/octet-stream"), file);
    }

    private RequestBody createPartFromString(String descriptionString) {
        return RequestBody.create(MediaType.parse("text/plain"), descriptionString);
    }

    public Map<String, String> getBodyMap() {
        return bodyMap;
    }

    public List<FileBody> getFileList() {
        if (fileList == null) {
            return new ArrayList<>();
        }
        return fileList;
    }
}
