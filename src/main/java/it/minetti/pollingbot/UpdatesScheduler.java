package it.minetti.pollingbot;

import it.minetti.FeaturesEntryPoint;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.annotation.PreDestroy;
import java.util.concurrent.BlockingQueue;

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
            entryPoint.process(update);
        }
    }

    @PreDestroy
    public void stop() {
        enabled = false;
    }
}