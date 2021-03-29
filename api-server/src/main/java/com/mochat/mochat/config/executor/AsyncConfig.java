package com.mochat.mochat.config.executor;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @description:
 * @author: Andy
 * @time: 2020/12/11 17:29
 */
@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {

//    @Value("${async.executor.corePoolSize}")
//    private Integer corePoolSize;
//    @Value("${async.executor.maxPoolSize}")
//    private Integer maxPoolSize;
//    @Value("${async.executor.queueCapacity}")
//    private Integer queueCapacity;
//    @Value("${async.executor.keepAliveSeconds}")
//    private Integer keepAliveSeconds;
//    @Value("${async.executor.threadNamePrefix}")
//    private String threadNamePrefix;
//
//    @Override
//    public Executor getAsyncExecutor() {
//        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
//
//        // 设置核心线程数
//        executor.setCorePoolSize(corePoolSize);
//        // 设置最大线程数
//        executor.setMaxPoolSize(maxPoolSize);
//        // 设置队列容量
//        executor.setQueueCapacity(queueCapacity);
//        // 设置线程活跃时间（秒）
//        executor.setKeepAliveSeconds(keepAliveSeconds);
//        // 设置默认线程名称
//        executor.setThreadNamePrefix(threadNamePrefix);
//        // 设置拒绝策略
//        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
//        // 等待所有任务结束后再关闭线程池
//        executor.setWaitForTasksToCompleteOnShutdown(true);
//        //初始化
//        executor.initialize();
//
//        return executor;
//    }
//
//    @Override
//    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
//        return null;
//    }
}
