package it.minetti.feature;

import it.minetti.persistence.ChatInfo;
import it.minetti.persistence.ChatRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.text.MessageFormat.format;
import static java.util.regex.Pattern.CASE_INSENSITIVE;

@Component
public class LanguageFeature implements Feature {

    @Autowired
    private AbsSender bot;

    @Autowired
    private ChatRepository repository;


    @Override
    public boolean test(Update update, String status, Locale userLocale) {
        if (status == null && update.hasMessage() && update.getMessage().hasText()) {
            ResourceBundle bundle = ResourceBundle.getBundle("messages", userLocale);
            Message message = update.getMessage();
            String text = message.getText();
            return StringUtils.containsIgnoreCase(text, bundle.getString("lang.trigger"));
        }
        return false;
    }

    @Override
    public void process(Update update, String status, Locale userLocale) throws TelegramApiException, InterruptedException {
        ResourceBundle bundle = ResourceBundle.getBundle("messages", userLocale);

        Message message = update.getMessage();
        String chatId = "" + message.getChatId();

        String languageText = bundle.getString("lang.trigger");
        Pattern msgPattern = Pattern.compile(languageText + "\\s+([a-z]+)", CASE_INSENSITIVE);
        Matcher matcher = msgPattern.matcher(message.getText());
        if (matcher.matches()) {
            Locale newLocale = new Locale(matcher.group(1)); // check for possible exceptions
            bot.execute(new SendMessage(chatId, format(bundle.getString("lang.done"), newLocale)));
            Optional<ChatInfo> chatInfo = repository.findById(chatId); // TODO use the chatInfo already found in entry point
            if (chatInfo.isPresent()) {
                chatInfo.get().setLocale(newLocale);
                repository.save(chatInfo.get());
            }
        } else {
            bot.execute(new SendMessage(chatId, "Sorry cannot understand. Please type '" + languageText + " en'" +
                    " to setup your language, change en with your language"));
        }
    }

}
