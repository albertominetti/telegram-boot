package it.minetti.webhookedbot;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

@Slf4j
@Component
@Deprecated // use the PollingBot
@ConditionalOnProperty(prefix = "bot", name = "base-webhook-url")
public class WebhookedBot extends TelegramWebhookBot {
    @Value("${bot.token}")
    private String token;

    @Value("${bot.username}")
    private String username;

    @Override
    public String getBotToken() {
        return token;
    }

    @Override
    public String getBotUsername() {
        return username;
    }


    @Override
    public BotApiMethod onWebhookUpdateReceived(Update update) {
        throw new NotImplementedException();
    }

    @Override
    public String getBotPath() {
        return "/" + token + "/webhook";
    }
}
