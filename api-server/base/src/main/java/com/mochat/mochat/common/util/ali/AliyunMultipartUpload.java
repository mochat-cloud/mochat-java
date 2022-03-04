package com.mochat.mochat.common.util.ali;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.*;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author: Ypw / ypwcode@163.com
 * @time: 2020/12/11 10:23 上午
 * @description 阿里云分片上传, 用于大文件上传
 */
@Slf4j
public class AliyunMultipartUpload {

    /**
     * 分片上传, 每片大小
     */
    private static final int PART_SIZE = 5 * 1024 * 1024;
    private static final int PART_COUNT_MAX = 10000;

    private static AliyunOssProperties ossProperties = AliyunComponent.ossProperties;

    private static OSS client;

    private ExecutorService executorService = Executors.newFixedThreadPool(5);
    private List<PartETag> partETags = Collections.synchronizedList(new ArrayList<>());

    public AliyunMultipartUpload() {
        client = AliyunComponent.getClient();
    }

    public void uploadFile(File file, String key) {
        if (null == file || file.length() < 1) {
            return;
        }

        try {

            String uploadId = getUploadId(key);

            long fileLength = file.length();
            int partCount = (int) (fileLength / PART_SIZE);
            if (fileLength % PART_SIZE != 0) {
                partCount++;
            }
            if (partCount > PART_COUNT_MAX) {
                log.debug("Total parts count should not exceed 10000");
            } else {
                log.debug("Total parts count " + partCount + "\n");
            }

            log.debug("Begin to upload multiparts to OSS from a file\n");
            for (int i = 0; i < partCount; i++) {
                long startPos = i * PART_SIZE;
                long curPartSize = (i + 1 == partCount) ? (fileLength - startPos) : PART_SIZE;
                executorService.execute(new PartUploader(file, key, partETags, startPos, curPartSize, i + 1, uploadId));
            }

            /*
             * Waiting for all parts finished
             */
            executorService.shutdown();
            while (!executorService.isTerminated()) {
                try {
                    executorService.awaitTermination(5, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            /*
             * Verify whether all parts are finished
             */
            if (partETags.size() != partCount) {
                throw new IllegalStateException("Upload multiparts fail due to some parts are not finished yet");
            } else {
                log.debug("Succeed to complete multiparts into an object named " + key + "\n");
            }

            /*
             * View all parts uploaded recently
             */
            listAllParts(uploadId, key);

            /*
             * Complete to upload multiparts
             */
            completeMultipartUpload(uploadId, key);
        } catch (OSSException oe) {
            System.out.println("Caught an OSSException, which means your request made it to OSS, "
                    + "but was rejected with an error response for some reason.");
            System.out.println("Error Message: " + oe.getErrorMessage());
            System.out.println("Error Code:       " + oe.getErrorCode());
            System.out.println("Request ID:      " + oe.getRequestId());
            System.out.println("Host ID:           " + oe.getHostId());
        } catch (ClientException ce) {
            System.out.println("Caught an ClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with OSS, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message: " + ce.getMessage());
        } finally {
            /*
             * Do not forget to shut down the client finally to release all allocated resources.
             */
            if (client != null) {
                client.shutdown();
            }
        }
    }

    private static class PartUploader implements Runnable {

        private File localFile;
        private String key;
        private List<PartETag> partETags;

        private long startPos;
        private long partSize;
        private int partNumber;
        private String uploadId;

        public PartUploader(File localFile, String key, List<PartETag> partETags, long startPos, long partSize, int partNumber, String uploadId) {
            this.localFile = localFile;
            this.key = key;
            this.partETags = partETags;
            this.startPos = startPos;
            this.partSize = partSize;
            this.partNumber = partNumber;
            this.uploadId = uploadId;
        }

        @Override
        public void run() {
            InputStream instream = null;
            try {
                instream = new FileInputStream(this.localFile);
                instream.skip(this.startPos);

                UploadPartRequest uploadPartRequest = new UploadPartRequest();
                uploadPartRequest.setBucketName(ossProperties.getBucketName());
                uploadPartRequest.setKey(key);
                uploadPartRequest.setUploadId(this.uploadId);
                uploadPartRequest.setInputStream(instream);
                uploadPartRequest.setPartSize(this.partSize);
                uploadPartRequest.setPartNumber(this.partNumber);

                UploadPartResult uploadPartResult = client.uploadPart(uploadPartRequest);
                log.debug("Part#" + this.partNumber + " done\n");
                synchronized (partETags) {
                    partETags.add(uploadPartResult.getPartETag());
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (instream != null) {
                    try {
                        instream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private String getUploadId(String key) {
        // 创建InitiateMultipartUploadRequest对象。
        InitiateMultipartUploadRequest request = new InitiateMultipartUploadRequest(ossProperties.getBucketName(), key);

        // 如果需要在初始化分片时设置文件存储类型，请参考以下示例代码。
        // ObjectMetadata metadata = new ObjectMetadata();
        // metadata.setHeader(OSSHeaders.OSS_STORAGE_CLASS, StorageClass.Standard.toString());
        // request.setObjectMetadata(metadata);

        // 初始化分片
        InitiateMultipartUploadResult result = client.initiateMultipartUpload(request);
        // 返回uploadId，它是分片上传事件的唯一标识，您可以根据这个uploadId发起相关的操作，如取消分片上传、查询分片上传等。
        return result.getUploadId();
    }

    private void completeMultipartUpload(String uploadId, String key) {
        // Make part numbers in ascending order
        Collections.sort(partETags, new Comparator<PartETag>() {

            @Override
            public int compare(PartETag p1, PartETag p2) {
                return p1.getPartNumber() - p2.getPartNumber();
            }
        });

        CompleteMultipartUploadRequest completeMultipartUploadRequest =
                new CompleteMultipartUploadRequest(ossProperties.getBucketName(), key, uploadId, partETags);

        // 如果需要在完成文件上传的同时设置文件访问权限，请参考以下示例代码。
        completeMultipartUploadRequest.setObjectACL(CannedAccessControlList.Private);

        client.completeMultipartUpload(completeMultipartUploadRequest);
    }

    private void listAllParts(String uploadId, String key) {
        System.out.println("Listing all parts......");
        ListPartsRequest listPartsRequest = new ListPartsRequest(ossProperties.getBucketName(), key, uploadId);
        PartListing partListing = client.listParts(listPartsRequest);

        int partCount = partListing.getParts().size();
        for (int i = 0; i < partCount; i++) {
            PartSummary partSummary = partListing.getParts().get(i);
            log.debug("\tPart#" + partSummary.getPartNumber() + ", ETag=" + partSummary.getETag());
        }
        log.debug("\n");
    }

    public static void upload(File file, String key) {
        new AliyunMultipartUpload().uploadFile(file, key);
    }

}
