package it.minetti.feature;

import it.minetti.persistence.ChatInfo;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public interface Feature {
    boolean test(Message message, ChatInfo chatInfo);

    void process(Message message, ChatInfo chatInfo) throws TelegramApiException, InterruptedException;
}
