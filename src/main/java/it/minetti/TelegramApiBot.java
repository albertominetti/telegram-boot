package it.minetti;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.annotation.PostConstruct;
import java.util.concurrent.BlockingQueue;

@Component
@Slf4j
public class TelegramApiBot extends TelegramLongPollingBot {
    private final String token;
    private final String username;

    @Autowired
    private BlockingQueue<Update> updatesQueue;

    public TelegramApiBot(@Value("${bot.token}") String token,
                          @Value("${bot.username}") String username) {
        this.token = token;
        this.username = username;
    }

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
