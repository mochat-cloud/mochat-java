package com.mochat.mochat.common.util.ali;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.CannedAccessControlList;
import com.aliyun.oss.model.CreateBucketRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author: yangpengwei
 * @time: 2020/12/11 12:45 下午
 * @description 阿里云配置生成工具类
 *
 * @see #ossProperties - 获取阿里云 OSS 配置
 * @see #getClient() - 获取新的阿里云 OSS
 */
@Component
public class AliyunComponent {

    public static OssProperties ossProperties;

    @Autowired
    public void setOssProperties(OssProperties ossProperties) {
        AliyunComponent.ossProperties = ossProperties;
    }
    
    /**
     * Do not forget to shut down the client finally to release all allocated resources.
     *
     * client.shutdown();
     */
    public static OSS getClient() {
        OSS client = new OSSClientBuilder().build(
                ossProperties.getEndpoint(),
                ossProperties.getAccessKeyId(),
                ossProperties.getAccessKeySecret());

        // 判断容器是否存在,不存在就创建
        if (!client.doesBucketExist(ossProperties.getBucketName())) {
            client.createBucket(ossProperties.getBucketName());
            CreateBucketRequest createBucketRequest = new CreateBucketRequest(ossProperties.getBucketName());
            createBucketRequest.setCannedACL(CannedAccessControlList.Private);
            client.createBucket(createBucketRequest);
        }
        return client;
    }

}
