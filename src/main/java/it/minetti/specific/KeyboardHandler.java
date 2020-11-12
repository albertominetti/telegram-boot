package it.minetti.specific;

import com.google.common.collect.Lists;
import it.minetti.TelegramApiBot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.InputStream;
import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;

@Component
public class KeyboardHandler implements BaseHandler {
    public static final InputStream MONKEY_IMAGE = TelegramApiBot.class.getResourceAsStream("/images/1473231766-scimmia.jpg");

    @Autowired
    TelegramApiBot bot;

    private static final Set<String> TRIGGER_MESSAGES = newHashSet("keyboard");

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
        SendMessage response = new SendMessage(chatId, "Below your keyboard");
        KeyboardRow keyboardRow1 = new KeyboardRow();
        keyboardRow1.add("Hi");
        keyboardRow1.add("Hello");
        KeyboardRow keyboardRow2 = new KeyboardRow();
        keyboardRow2.add("Ciao");
        response.setReplyMarkup(new ReplyKeyboardMarkup(Lists.newArrayList(keyboardRow1, keyboardRow2)));
        bot.execute(response);
    }
}
