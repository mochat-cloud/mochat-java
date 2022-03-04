package com.mochat.mochat.model.properties;

import java.io.Serializable;

/**
 * @description:文件属性
 * @author: Huayu
 * @time: 2020/11/22 18:00
 */
public class FileProperties implements Serializable {

    private String name;//文件名
    private String mimeType;//文件类型
    private String path;//文件路径
    private String fullPath;//文件全路径

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getFullPath() {
        return fullPath;
    }

    public void setFullPath(String fullPath) {
        this.fullPath = fullPath;
    }

    public FileProperties(String name, String mimeType, String path, String fullPath) {
        this.name = name;
        this.mimeType = mimeType;
        this.path = path;
        this.fullPath = fullPath;
    }
}
