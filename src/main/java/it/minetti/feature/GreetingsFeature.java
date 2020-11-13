package it.minetti.feature;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendChatAction;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.ResourceBundle;

import static java.util.Arrays.asList;

@Component
public class GreetingsFeature implements Feature {

    @Autowired
    AbsSender bot;

    private final Random rand = new Random();

    @Override
    public boolean test(Update update, String status, Locale userLocale) {
        if (status == null && update.hasMessage() && update.getMessage().hasText()) {
            ResourceBundle bundle = ResourceBundle.getBundle("messages", userLocale);
            Message message = update.getMessage();
            String text = message.getText();
            List<String> triggers = asList(bundle.getString("hello.trigger").split(","));
            return triggers.contains(text.toLowerCase());
        }
        return false;
    }

    @Override
    public void process(Update update, String status, Locale userLocale) throws TelegramApiException, InterruptedException {
        ResourceBundle bundle = ResourceBundle.getBundle("messages", userLocale);
        Message message = update.getMessage();
        String chatId = "" + message.getChatId();
        bot.execute(new SendChatAction(chatId, "typing"));
        Thread.sleep(200);
        List<String> triggers = asList(bundle.getString("hello.trigger").split(","));
        String greeting = triggers.get(rand.nextInt(triggers.size()));
        greeting = StringUtils.capitalize(greeting) + "!";
        bot.execute(new SendMessage(chatId, greeting));
    }
}
