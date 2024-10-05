package com.jhsfully.dynamicbatch.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class AdjustmentTaskExecutorConfig {

    @Bean("adjustmentTaskExecutor")
    public TaskExecutor adjustmentTaskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(4);
        taskExecutor.setMaxPoolSize(8);
        taskExecutor.setThreadNamePrefix("batch-async-thread");
        return taskExecutor;
    }

}
