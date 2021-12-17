package com.mochat.mochat.controller;

import com.alibaba.fastjson.JSONObject;
import com.mochat.mochat.common.util.FileUtils;
import com.mochat.mochat.common.util.ali.AliyunOssUtils;
import com.mochat.mochat.common.util.wm.ApiRespUtils;
import com.mochat.mochat.config.ex.ParamException;
import com.mochat.mochat.model.ApiRespVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

/**
 * @description:上传
 * @author: Huayu
 * @time: 2020/11/22 14:30
 */
@RestController
@RequestMapping("/common")
public class UploadController {

    private final static Logger logger = LoggerFactory.getLogger(LoginController.class);

    /**
     * @param file
     * @description: 上传文件
     * @return:
     * @author: Huayu
     * @time: 2020/11/22 14:38
     */
    @PostMapping("/upload")
    public ApiRespVO uploadBlog(@RequestParam("file") MultipartFile file, @RequestParam(value = "name", defaultValue = "") String name, @RequestParam(value = "path", defaultValue = "") String path) throws IOException {
        if (file == null || file.getSize() < 1) {
            throw new ParamException("文件不能为空");
        }

        String mimeType = file.getContentType();
        String fileName = "".equals(name) ? file.getOriginalFilename() : name;
        String fileSuffix = fileName.substring(fileName.lastIndexOf("."));
        String key = "".equals(path) ? FileUtils.getFileName("upload/", fileSuffix) : path + fileName;
        File tempFile = File.createTempFile("" + System.currentTimeMillis(), fileSuffix);
        file.transferTo(tempFile);
        String fileUrl = AliyunOssUtils.upLoadAndGetUrl(tempFile, key);
        tempFile.delete();
        //判断文件是否为空
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", fileName);
        jsonObject.put("mimeType", mimeType);
        jsonObject.put("path", key);
        jsonObject.put("fullPath", fileUrl);
        return ApiRespUtils.ok(jsonObject);
    }

}
