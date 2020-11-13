package it.minetti.pollingbot;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.annotation.PostConstruct;
import java.util.concurrent.BlockingQueue;

@Slf4j
@Component
@ConditionalOnProperty(prefix = "bot", name = "base-webhook-url", matchIfMissing = true)
public class PollingBot extends TelegramLongPollingBot {
    @Value("${bot.token}")
    private String token;
    @Value("${bot.username}")
    private String username;

    @Autowired
    private BlockingQueue<Update> updatesQueue;

    @Override
    public String getBotToken() {
        return token;
    }

    @Override
    public String getBotUsername() {
        return username;
    }

    @Override
    public void onUpdateReceived(Update update) {
        log.debug("Update: {}", update);
        updatesQueue.add(update);
    }

    @PostConstruct
    public void start() {
        log.info("username: {}, token: {}", username, token);
    }

}
