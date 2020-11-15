package it.minetti.feature;

import it.minetti.persistence.ChatInfo;
import it.minetti.persistence.ChatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.math.BigDecimal;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;

import static java.math.BigDecimal.ZERO;
import static java.text.MessageFormat.format;
import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;

@Component
public class SumFeature implements Feature {

    private static final String SUM = "SUM";

    @Autowired
    private AbsSender bot;

    @Autowired
    private ChatRepository repository;

    private final Map<String, BigDecimal> totals = new ConcurrentHashMap<>();

    @Override
    public boolean test(Message message, ChatInfo chatInfo) {
        if (SUM.equals(chatInfo.getStatus()) && message.hasText()) {
            return true;
        }
        if (chatInfo.getStatus() == null && message.hasText()) {
            ResourceBundle bundle = ResourceBundle.getBundle("messages", chatInfo.getLocale());
            String text = message.getText();
            return equalsIgnoreCase(text, bundle.getString("sum.trigger")) || equalsIgnoreCase(text, "/sum");
        }
        return false;
    }

    @Override
    public void process(Message message, ChatInfo chatInfo) throws TelegramApiException {
        ResourceBundle bundle = ResourceBundle.getBundle("messages", chatInfo.getLocale());

        String chatId = chatInfo.getChatId();
        String sumTrigger = bundle.getString("sum.trigger");
        String text = message.getText();
        if (chatInfo.getStatus() == null && (equalsIgnoreCase(text, sumTrigger) || equalsIgnoreCase(text, "/sum"))) {
            totals.put(chatId, ZERO);
            chatInfo.setStatus(SUM);
            repository.save(chatInfo);
            bot.execute(new SendMessage(chatId, bundle.getString("sum.help")));
            return;
        }

        if (SUM.equals(chatInfo.getStatus()) && equalsIgnoreCase(text, bundle.getString("sum.stop"))) {
            endFeature(chatInfo);
            bot.execute(new SendMessage(chatId, bundle.getString("sum.done")));
            return;
        }

        if (SUM.equals(chatInfo.getStatus())) {
            try {
                text = text.replace(',', '.');
                BigDecimal value = new BigDecimal(text);
                BigDecimal newTotal = totals.computeIfPresent(chatId, (k, t) -> t.add(value));
                newTotal = newTotal.stripTrailingZeros();
                bot.execute(new SendMessage(chatId, format(bundle.getString("sum.total"), newTotal)));
            } catch (IllegalArgumentException e) {
                chatInfo.setStatus(null);
                repository.save(chatInfo);
                bot.execute(new SendMessage(chatId, bundle.getString("sum.error")));
            }
        }

    }

    private void endFeature(ChatInfo chatInfo) {
        totals.remove(chatInfo.getChatId());
        chatInfo.setStatus(null);
        repository.save(chatInfo);
    }


}
