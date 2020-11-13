package it.minetti.feature;

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

import java.util.Locale;
import java.util.ResourceBundle;

@Component
public class MonkeyImageFeature implements Feature {

    public static final String ACTION_TYPING = "typing";
    public static final String ACTION_UPLOAD_PHOTO = "upload_photo";
    @Autowired
    AbsSender bot;

    @Override
    public boolean test(Update update, String status, Locale userLocale) {
        if (status == null && update.hasMessage() && update.getMessage().hasText()) {
            ResourceBundle bundle = ResourceBundle.getBundle("messages", userLocale);
            Message message = update.getMessage();
            String text = message.getText();
            return bundle.getString("monkey").equalsIgnoreCase(text);
        }
        return false;
    }

    @Override
    public void process(Update update, String status, Locale userLocale) throws TelegramApiException, InterruptedException {
        ResourceBundle bundle = ResourceBundle.getBundle("messages", userLocale);

        Message message = update.getMessage();
        String chatId = "" + message.getChatId();
        bot.execute(new SendChatAction(chatId, ACTION_TYPING));
        Thread.sleep(200);
        bot.execute(new SendMessage(chatId, bundle.getString("you.monkey")));
        Thread.sleep(500);
        bot.execute(new SendChatAction(chatId, ACTION_UPLOAD_PHOTO));
        bot.execute(new SendPhoto(chatId, new InputFile(
                this.getClass().getResourceAsStream("/images/1473231766-scimmia.jpg"), bundle.getString("monkey"))
        ));
    }

}
