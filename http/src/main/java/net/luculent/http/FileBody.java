package net.luculent.http;

import java.io.File;

/**
 * Created by xiayanlei on 2018/7/17.
 */

public class FileBody {

    private String name;
    private File file;

    public FileBody(String name, File file) {
        this.name = name;
        this.file = file;
    }

    public String getName() {
        return name == null ? "file" : name;
    }

    public File getFile() {
        return file;
    }
}
