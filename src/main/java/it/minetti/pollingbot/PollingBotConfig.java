package it.minetti.pollingbot;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.BotSession;
import org.telegram.telegrambots.meta.generics.LongPollingBot;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Slf4j
@Configuration
@ConditionalOnBean(PollingBot.class)
public class PollingBotConfig {
    private BotSession session;

    @Autowired
    private LongPollingBot pollingBot;

    @Autowired
    private UpdatesScheduler updatesScheduler;

    @PostConstruct
    public void start() throws TelegramApiException {
        log.info("Starting auto config for telegram bot");
        TelegramBotsApi api = new TelegramBotsApi(DefaultBotSession.class);

        try {
            log.info("Registering polling bot: {}", pollingBot.getBotUsername());
            session = api.registerBot(pollingBot);
        } catch (TelegramApiException var4) {
            log.error("Failed to register bot {} due to error", pollingBot.getBotUsername(), var4);
        }

        updatesScheduler.scheduleUpdate();
    }


    @PreDestroy
    public void stop() {
        if (session != null) {
            session.stop();
        }
    }
}
