package it.minetti.feature;

import it.minetti.persistence.ChatInfo;
import it.minetti.persistence.ChatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.collect.Sets.newHashSet;
import static java.text.MessageFormat.format;
import static java.util.regex.Pattern.CASE_INSENSITIVE;
import static org.apache.commons.lang3.StringUtils.lowerCase;
import static org.apache.commons.lang3.StringUtils.startsWith;

@Component
public class LanguageFeature implements Feature {

    public static final Set<String> AVAILABLE_LANGUAGES = newHashSet("it", "en");

    @Autowired
    private AbsSender bot;

    @Autowired
    private ChatRepository repository;


    @Override
    public boolean test(Message message, ChatInfo chatInfo) {
        if (chatInfo.getStatus() == null && message.hasText()) {
            ResourceBundle bundle = ResourceBundle.getBundle("messages", chatInfo.getLocale());
            String text = message.getText();
            return startsWith(text, bundle.getString("lang.trigger"));
        }
        return false;
    }

    @Override
    public void process(Message message, ChatInfo chatInfo) throws TelegramApiException {
        ResourceBundle bundle = ResourceBundle.getBundle("messages", chatInfo.getLocale());

        String chatId = chatInfo.getChatId();

        String languageTrigger = bundle.getString("lang.trigger");
        Pattern msgPattern = Pattern.compile(languageTrigger + "\\s+([a-z]+)", CASE_INSENSITIVE);
        Matcher matcher = msgPattern.matcher(message.getText());
        if (matcher.matches()) {
            String chosenLang = matcher.group(1);

            if (AVAILABLE_LANGUAGES.contains(lowerCase(chosenLang))) {
                Locale newLocale = new Locale(chosenLang);
                bot.execute(new SendMessage(chatId, format(bundle.getString("lang.done"), newLocale)));
                chatInfo.setLocale(newLocale);
                repository.save(chatInfo);
            } else {
                bot.execute(new SendMessage(chatId, format(bundle.getString("lang.not_supported"))));
            }
        } else {
            bot.execute(new SendMessage(chatId, format(bundle.getString("lang.help"), languageTrigger)));
        }
    }

}
