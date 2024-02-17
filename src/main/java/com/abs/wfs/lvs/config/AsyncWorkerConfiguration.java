package com.abs.wfs.lvs.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurerSupport;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncWorkerConfiguration extends AsyncConfigurerSupport {

    @Value("${ap.worker.pool-size.core}")
    private int corePoolSize;  // 기본 실행 대기하는 Thread 수
    @Value("${ap.worker.pool-size.max}")
    private int maxPoolSize;  // 동시 동작하는 최대 Thread 수
    @Value("${ap.worker.capacity}")
    private int queueCapacity;  // MaxPoolSize 초과 요청 시, 최대 Queue 저장 수
    @Value("${ap.worker.name.prefix}")
    private String threadPrefixName; // 생성되는 Thread 접두사 명

    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix(threadPrefixName);
        executor.initialize();
        return executor;
    }
}
