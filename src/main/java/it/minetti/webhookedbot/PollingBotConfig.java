package it.minetti.webhookedbot;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import javax.annotation.PostConstruct;

@Slf4j
@Deprecated // use the PollingBot
@Configuration
@ConditionalOnBean(WebhookedBot.class)
public class PollingBotConfig {

    @Autowired
    private TelegramWebhookBot webHookBot;

    @Value("${bot.base-webhook-url}")
    private String baseWebhookUrl;

    @PostConstruct
    public void start() throws TelegramApiException {
        log.info("Starting auto config for telegram bot");
        TelegramBotsApi api = new TelegramBotsApi(DefaultBotSession.class);

            try {
                log.info("Registering web hook bot: {}", webHookBot.getBotUsername());
                api.registerBot(webHookBot, new SetWebhook(baseWebhookUrl.concat(webHookBot.getBotPath())));
            } catch (TelegramApiException var3) {
                log.error("Failed to register bot {} due to error", webHookBot.getBotUsername(), var3);
            }

    }

}
