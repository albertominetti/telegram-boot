package it.minetti.feature;

import it.minetti.persistence.ChatInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendChatAction;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

@Component
public class MonkeyImageFeature implements Feature {

    public static final String ACTION_TYPING = "typing";
    public static final String ACTION_UPLOAD_PHOTO = "upload_photo";

    @Autowired
    private AbsSender bot;

    @Override
    public boolean test(Message message, ChatInfo chatInfo) {
        if (chatInfo.getStatus() == null && message.hasText()) {
            ResourceBundle bundle = ResourceBundle.getBundle("messages", chatInfo.getLocale());
            String text = message.getText();
            return bundle.getString("monkey").equalsIgnoreCase(text);
        }
        return false;
    }

    @Override
    public void process(Message message, ChatInfo chatInfo) throws TelegramApiException, InterruptedException {
        ResourceBundle bundle = ResourceBundle.getBundle("messages", chatInfo.getLocale());

        String chatId = chatInfo.getChatId();

        bot.execute(new SendChatAction(chatId, ACTION_TYPING));
        TimeUnit.MILLISECONDS.sleep(200);
        bot.execute(new SendMessage(chatId, bundle.getString("you.monkey")));

        bot.execute(new SendChatAction(chatId, ACTION_UPLOAD_PHOTO));
        TimeUnit.MILLISECONDS.sleep(500);
        bot.execute(new SendPhoto(chatId, new InputFile(
                this.getClass().getResourceAsStream("/images/1473231766-scimmia.jpg"), bundle.getString("monkey"))
        ));
    }

}
