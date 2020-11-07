package it.minetti;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.BotSession;
import org.telegram.telegrambots.meta.generics.LongPollingBot;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Slf4j
@Configuration
public class TelegramBotAutoConfiguration {
    private BotSession session;
    private final LongPollingBot pollingBot;
    private final TelegramWebhookBot webHookBot;

    public TelegramBotAutoConfiguration(LongPollingBot pollingBot, @Autowired(required = false) TelegramWebhookBot webHookBot) {
        this.pollingBot = pollingBot;
        this.webHookBot = webHookBot;
    }

    @PostConstruct
    public void start() throws TelegramApiException {
        log.info("Starting auto config for telegram bots");
        TelegramBotsApi api = telegramBotsApi();

        if (pollingBot != null) {
            try {
                log.info("Registering polling bot: {}", pollingBot.getBotUsername());
                session = api.registerBot(pollingBot);
            } catch (TelegramApiException var4) {
                log.error("Failed to register bot {} due to error", pollingBot.getBotUsername(), var4);
            }
        }

        if (webHookBot != null) {
            try {
                log.info("Registering web hook bot: {}", webHookBot.getBotUsername());
                api.registerBot(webHookBot, null /* TODO setup the webhook if needed*/);
            } catch (TelegramApiException var3) {
                log.error("Failed to register bot {} due to error", webHookBot.getBotUsername(), var3);
            }
        }

    }

    @Bean
    @ConditionalOnMissingBean
    public TelegramBotsApi telegramBotsApi() throws TelegramApiException {
        TelegramBotsApi result;
        log.info("Initializing API without webhook support");
        result = new TelegramBotsApi(DefaultBotSession.class);
        return result;
    }

    @PreDestroy
    public void stop() {
        if (session != null) {
            session.stop();
        }
    }
}
