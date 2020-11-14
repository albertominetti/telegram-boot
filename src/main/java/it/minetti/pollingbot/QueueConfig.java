package it.minetti.pollingbot;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.annotation.PostConstruct;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;

import static java.lang.Math.max;

@Slf4j
@Configuration
@ConditionalOnBean(PollingBot.class)
public class QueueConfig {
    public int poolSize = 1;

    @PostConstruct
    public void setUp() {
        int availableProcessors = Runtime.getRuntime().availableProcessors();
        poolSize = max(availableProcessors - 1, 1);
        log.info("Pool size is {}", poolSize);
    }

    @Bean
    public BlockingQueue<Update> updatesQueue() {
        return new LinkedBlockingQueue<>();
    }

    @Bean
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(poolSize);
        executor.setMaxPoolSize(poolSize);
        executor.setQueueCapacity(1000);
        executor.setThreadNamePrefix("thread-");
        executor.initialize();
        return executor;
    }
}