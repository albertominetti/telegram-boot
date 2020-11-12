package it.minetti.generic;

import it.minetti.TelegramApiBot;
import it.minetti.specific.BaseHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;

import java.util.List;

@Slf4j
@Component
public class GenericUpdateHandler {

    @Autowired
    TelegramApiBot bot;

    @Autowired
    List<BaseHandler> handlers;

    @Async
    public void handle(Update update) {
        boolean processed = false;
        try {
            for (BaseHandler handler : handlers) {
                if (handler.test(update, null)) {
                    handler.process(update, null);
                    processed = true;
                    break;
                }
            }
            if (!processed) {
                if (update.hasMessage()) {
                    String chatId = "" + update.getMessage().getChatId();
                    SendMessage response = new SendMessage(chatId, "Sorry, cannot understand.");
                    response.setReplyMarkup(new ReplyKeyboardRemove(true));
                    log.debug("Response: {}", response);
                    bot.execute(response);
                }
            }

        } catch (Exception e) {
            log.error("Failed due to error", e);
        }
    }

}
