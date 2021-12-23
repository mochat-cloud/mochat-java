package com.mochat.mochat.common.util.emp;

import com.mochat.mochat.common.constant.Const;
import com.mochat.mochat.common.util.HttpClientUtil;
import com.mochat.mochat.common.util.ali.AliyunOssUtils;

import java.io.File;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author: Ypw / ypwcode@163.com
 * @time: 2020/12/11 3:39 下午
 * @description 异步下载并上传至阿里云工具类
 */
public class DownUploadQueueUtils {

    private static final ExecutorService poolExecutor = new ThreadPoolExecutor(
            5, Integer.MAX_VALUE, 1, TimeUnit.MINUTES, new LinkedBlockingQueue<>(),
            new CustomThreadFactory(), new ThreadPoolExecutor.AbortPolicy()
    );

    public static void uploadFileByUrl(String fileName, String url) {
        poolExecutor.execute(new CustomTask(fileName, url));
    }

    /**
     * The default thread factory
     */
    private static class CustomThreadFactory implements ThreadFactory {
        private static final AtomicInteger poolNumber = new AtomicInteger(1);
        private final ThreadGroup group;
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;

        CustomThreadFactory() {
            SecurityManager s = System.getSecurityManager();
            group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
            namePrefix = "pool-" + poolNumber.getAndIncrement() + "-thread-";
        }

        CustomThreadFactory(String namePrefix) {
            SecurityManager s = System.getSecurityManager();
            group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
            this.namePrefix = namePrefix;
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(group, r,
                    namePrefix + threadNumber.getAndIncrement(),
                    0);
            if (t.isDaemon()) {
                t.setDaemon(false);
            }
            if (t.getPriority() != Thread.NORM_PRIORITY) {
                t.setPriority(Thread.NORM_PRIORITY);
            }
            return t;
        }
    }

    private static class CustomTask implements Runnable {

        private final String fileName;
        private final String url;

        public CustomTask(String fileName, String url) {
            this.fileName = fileName;
            this.url = url;
        }

        @Override
        public void run() {
            try {
                // 下载文件
                HttpClientUtil.doGetDownload(url, new File(Const.TEMP_FILE_DIR, fileName));
                // 上传文件
                AliyunOssUtils.upLoad(new File(Const.TEMP_FILE_DIR, fileName), fileName);
            } catch (Exception e) {}
        }
    }

}
