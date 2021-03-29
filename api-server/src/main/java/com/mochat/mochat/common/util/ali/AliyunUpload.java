package com.mochat.mochat.common.util.ali;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.internal.OSSHeaders;
import com.aliyun.oss.model.CannedAccessControlList;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PutObjectRequest;
import com.aliyun.oss.model.StorageClass;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * @author: yangpengwei
 * @time: 2020/12/11 12:43 下午
 * @description 阿里云简单文件上传
 */
@Slf4j
public class AliyunUpload {

    private static OssProperties ossProperties = AliyunComponent.ossProperties;

    public static void upLoad(File file, String key) {
        if (null == file || file.length() < 1) {
            return;
        }

        OSS client = AliyunComponent.getClient();

        try {
            // key 表示上传文件到OSS时需要指定包含文件后缀在内的完整路径，例如abc/efg/123.jpg。
            PutObjectRequest putObjectRequest = new PutObjectRequest(ossProperties.getBucketName(), key, file);

            // 如果需要上传时设置存储类型与访问权限，请参考以下示例代码。
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setHeader(OSSHeaders.OSS_STORAGE_CLASS, StorageClass.Standard.toString());
            metadata.setObjectAcl(CannedAccessControlList.Private);
            putObjectRequest.setMetadata(metadata);

            // 上传字符串。
            client.putObject(putObjectRequest);

            // 关闭OSSClient。
            client.shutdown();
        } catch (OSSException oe) {
            log.error(oe.getMessage());
        } catch (ClientException ce) {
            log.error(ce.getErrorMessage());
        } finally {
            if (client != null) {
                client.shutdown();
            }
        }
    }

    public static void uploadStream(String fileUrl, String key) {
        OSS client = AliyunComponent.getClient();
        try {
            InputStream inputStream = new URL(fileUrl).openStream();
            client.putObject(ossProperties.getBucketName(), key, inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            client.shutdown();
        }
    }
}

