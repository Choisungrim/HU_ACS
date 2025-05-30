package com.hubis.acs.common.configuration.thread;

import jakarta.annotation.PreDestroy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class AsyncConfig {

    // 재사용 가능한 Executor 캐시
    private final ConcurrentHashMap<String, TaskExecutor> executorMap = new ConcurrentHashMap<>();

    /**
     * 동적으로 Executor를 생성하거나 반환
     * @param name 스레드 이름 prefix
     * @param size 코어/최대 스레드 수
     * @return TaskExecutor
     */
    public TaskExecutor getOrCreateExecutor(String name, int size) {
        return executorMap.computeIfAbsent(name, key -> {
            ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
            executor.setCorePoolSize(size);
            executor.setMaxPoolSize(size);
            executor.setQueueCapacity(100);  // 필요시 설정 변경 가능
            executor.setThreadNamePrefix(name + "-");
            executor.initialize();
            return executor;
        });
    }

    @PreDestroy
    public void shutdownExecutors() {
        executorMap.values().forEach(executor -> {
            if (executor instanceof ThreadPoolTaskExecutor threadPool) {
                threadPool.shutdown();
            }
        });
    }

}
