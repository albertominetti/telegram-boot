package it.minetti.handler.specific;

import it.minetti.handler.BaseHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendChatAction;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;

@Component
public class MonkeyImageHandler implements BaseHandler {

    @Autowired
    AbsSender bot;

    private static final Set<String> TRIGGER_MESSAGES = newHashSet("monkey", "scimmia", "ape");

    @Override
    public boolean test(Update update, String status) {
        if (status == null && update.hasMessage() && update.getMessage().hasText()) {
            Message message = update.getMessage();
            String text = message.getText();
            return TRIGGER_MESSAGES.contains(text.toLowerCase());
        }
        return false;
    }

    @Override
    public void process(Update update, String status) throws TelegramApiException, InterruptedException {
        Message message = update.getMessage();
        String chatId = "" + message.getChatId();
        bot.execute(new SendChatAction(chatId, "typing"));
        Thread.sleep(200);
        bot.execute(new SendMessage(chatId, "Me?? You are the monkey!"));
        Thread.sleep(500);
        bot.execute(new SendChatAction(chatId, "upload_photo"));
        bot.execute(new SendPhoto(chatId, new InputFile(
                this.getClass().getResourceAsStream("/images/1473231766-scimmia.jpg"), "monkey")
        ));
    }
}
