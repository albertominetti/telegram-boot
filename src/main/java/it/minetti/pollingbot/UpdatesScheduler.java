package it.minetti.pollingbot;

import it.minetti.feature.entrypoint.FeaturesEntryPoint;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.annotation.PreDestroy;
import java.util.concurrent.BlockingQueue;

@Slf4j
@Component
@ConditionalOnBean(PollingBot.class)
public class UpdatesScheduler {
    @Autowired
    private FeaturesEntryPoint entryPoint;

    @Autowired
    private BlockingQueue<Update> updatesQueue;

    private boolean enabled = true;

    @Async
    @SneakyThrows
    public void scheduleUpdate() {
        while (enabled) {
            Update update = updatesQueue.take();
            log.debug("Message removed from the queue");
            entryPoint.process(update);
        }
    }

    @PreDestroy
    public void stop() {
        enabled = false;
    }
}