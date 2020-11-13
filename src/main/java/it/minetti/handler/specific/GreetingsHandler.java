package it.minetti.handler.specific;

import it.minetti.handler.BaseHandler;
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
import java.util.Random;

import static com.google.common.collect.Lists.newArrayList;

@Component
public class GreetingsHandler implements BaseHandler {
    private static final List<String> TRIGGER_MESSAGES = newArrayList("hello", "hi", "ciao");

    @Autowired
    AbsSender bot;

    private final Random rand = new Random();

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
        String greeting = TRIGGER_MESSAGES.get(rand.nextInt(TRIGGER_MESSAGES.size()));
        greeting = StringUtils.capitalize(greeting) + "!";
        bot.execute(new SendMessage(chatId, greeting));
    }
}
