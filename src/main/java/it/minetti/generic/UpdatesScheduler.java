package it.minetti.generic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.annotation.PreDestroy;
import java.util.concurrent.BlockingQueue;

@Component
public class UpdatesScheduler {
    @Autowired
    private GenericUpdateHandler handler;

    @Autowired
    private BlockingQueue<Update> updatesQueue;

    private boolean enabled = true;

    @Async
    public void scheduleUpdate() {
        while (enabled) {
            try {
                Update update = updatesQueue.take();
                handler.handle(update);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @PreDestroy
    public void stop() {
        enabled = false;
    }
}