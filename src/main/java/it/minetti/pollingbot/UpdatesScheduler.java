package it.minetti.pollingbot;

import it.minetti.handler.MainHandler;
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
    private MainHandler handler;

    @Autowired
    private BlockingQueue<Update> updatesQueue;

    private boolean enabled = true;

    @Async
    public void scheduleUpdate() throws InterruptedException {
        while (enabled) {
            Update update = updatesQueue.take();
            handler.handle(update);
        }
    }

    @PreDestroy
    public void stop() {
        enabled = false;
    }
}