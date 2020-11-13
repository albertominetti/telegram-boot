package it.minetti;

import it.minetti.feature.Feature;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

@Slf4j
@Component
public class FeaturesEntryPoint {

    @Autowired
    AbsSender bot;

    @Autowired
    List<Feature> features;

    @Async
    public void process(Update update) throws TelegramApiException {
        boolean processed = false;

        for (Feature feature : features) {
            try {
                if (feature.test(update, null)) {
                    feature.process(update, null);
                    processed = true;
                    break;
                }
            } catch (Exception e) {
                log.error("Failed due to error", e);
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

    }

}
