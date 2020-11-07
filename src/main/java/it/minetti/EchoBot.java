package it.minetti;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendChatAction;
import org.telegram.telegrambots.meta.api.methods.send.SendLocation;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.annotation.PostConstruct;

@Component
@Slf4j
public class EchoBot extends TelegramLongPollingBot {
    private final String token;
    private final String username;

    public EchoBot(@Value("${bot.token}") String token, @Value("${bot.username}") String username) {
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

    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {
        log.debug("Update: {}", update);

        if (update.hasMessage()) {
            Message message = update.getMessage();
            Long chatId = message.getChatId();
            String text = message.getText();

            execute(new SendChatAction("" + chatId, "typing"));

            Thread.sleep(500);

            SendMessage response = new SendMessage("" + chatId, text);
            //response.setReplyToMessageId(update.getMessage().getMessageId());

            log.debug("Response: {}", response);

            try {
                execute(response);
                execute(new SendLocation("" + chatId, 44.558780, 8.674270));
                log.info("Sent message \"{}\" to {}", text, chatId);
            } catch (TelegramApiException e) {
                log.error("Failed to send message \"{}\" to {} due to error: {}", text, chatId, e.getMessage());
            }
        }
    }

    @PostConstruct
    public void start() {
        log.info("username: {}, token: {}", username, token);
    }

}
