package com.mochat.mochat.common.util.ali;

import com.aliyun.oss.OSS;
import com.aliyun.oss.model.GetObjectRequest;
import com.mochat.mochat.common.util.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @description:
 * @author: Huayu
 * @time: 2020/11/22 15:05
 * @see #upLoadAndGetUrl(File, String) - 上传文件并获取访问 url
 * @see #upLoad(File, String) - 上传文件
 */
public class AliyunOssUtils {

    private static final int LIMIT = 10 * 1024 * 1024;

    private static OssProperties ossProperties = AliyunComponent.ossProperties;

    @Autowired
    public void setOssProperties(OssProperties ossProperties) {
        AliyunOssUtils.ossProperties = ossProperties;
    }

    /**
     * 上传文件并获取访问 url
     *
     * @param file 需要上传的文件
     * @param key  文件要保存到阿里云的目录和文件名
     * @return 文件临时访问 url, 失效期为 1 小时, 返回 null 表示文件上传失败
     */
    public static String upLoadAndGetUrl(File file, String key) {
        if (upLoad(file, key)) {
            return getUrl(key);
        }
        // 文件上传失败
        return null;
    }

    /**
     * @param fileUrlMap <图片网络地址,表示上传文件到OSS时需要指定包含文件后缀在内的完整路径，例如abc/efg/123.jpg。>
     * @description 多头像上传
     * @author zhaojinjian
     * @createTime 2020/12/23 15:18
     */
    public static void multipleUpLoad(Map<String, String> fileUrlMap) {
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(10);
        for (Map.Entry<String, String> fileUrlInfo : fileUrlMap.entrySet()) {
            fixedThreadPool.execute(new Runnable() {
                public void run() {
                    AliyunUpload.uploadStream(fileUrlInfo.getKey(), fileUrlInfo.getValue());
                }
            });
        }
    }

    /**
     * 上传文件并获取访问 url
     *
     * @param file 需要上传的文件
     * @param key  文件保存到阿里云的目录和文件名
     */
    public static boolean upLoad(File file, String key) {
        if (null == file || file.length() < 1) {
            return false;
        }
        if (file.length() > LIMIT) {
            AliyunMultipartUpload.upload(file, key);
        } else {
            AliyunUpload.upLoad(file, key);
        }
        return true;
    }

    /**
     * 获取阿里云文件临时访问 url, 过期时间为 1 小时
     *
     * @param key 文件保存到阿里云的目录和文件名
     * @return url 文件临时访问 url
     */
    public static String getUrl(String key) {
        OSS client = AliyunComponent.getClient();
        // 设置URL过期时间为1小时。
        Date expiration = new Date(System.currentTimeMillis() + 1000 * 60 * 60);
        // 生成以GET方法访问的签名URL，访客可以直接通过浏览器访问相关内容。
        URL url = client.generatePresignedUrl(ossProperties.getBucketName(), key, expiration);
        client.shutdown();
        return url.toString();
    }

    public static File getFile(String key) {
        int index = key.lastIndexOf(".");
        String suffix = key.substring(index);
        File file = null;
        try {
            file = File.createTempFile(FileUtils.getRandomString(), suffix);
            OSS client = AliyunComponent.getClient();
            // 下载OSS文件到本地文件。如果指定的本地文件存在会覆盖，不存在则新建。
            client.getObject(new GetObjectRequest(ossProperties.getBucketName(), key), file);
            // 关闭OSSClient。
            client.shutdown();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }
}

