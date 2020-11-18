package it.minetti.feature;

import it.minetti.persistence.ChatInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;

import static com.google.common.collect.Lists.newArrayList;
import static java.text.MessageFormat.format;
import static java.util.Arrays.asList;
import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;

@Slf4j
@Component
public class HelpFeature implements Feature {

    public static final int KEYBOARD_MAX_COLUMNS = 3;

    @Autowired
    private AbsSender bot;

    @Override
    public boolean test(Message message, ChatInfo chatInfo) {
        if (chatInfo.getStatus() == null && message.hasText()) {
            ResourceBundle bundle = ResourceBundle.getBundle("messages", chatInfo.getLocale());
            String text = message.getText();
            return equalsIgnoreCase(text, bundle.getString("help.trigger"))
                    || equalsIgnoreCase(text, "/help");
        }
        return false;
    }

    @Override
    public void process(Message message, ChatInfo chatInfo) throws TelegramApiException {
        ResourceBundle bundle = ResourceBundle.getBundle("messages", chatInfo.getLocale());

        List<String> commands = asList(
                bundle.getString("lang.trigger"),
                bundle.getString("sum.trigger"),
                bundle.getString("monkey.trigger"),
                bundle.getString("hello.trigger").split(",")[0],
                bundle.getString("kitten.trigger").split(",")[0]
        );
        String commaSeparatedCommands = String.join(", ", commands);

        SendMessage response = new SendMessage(chatInfo.getChatId(), format(bundle.getString("help.message"), commaSeparatedCommands));

        ArrayList<KeyboardRow> keyboardRows = newArrayList();
        KeyboardRow currentKeyboardRow = new KeyboardRow();
        keyboardRows.add(currentKeyboardRow);
        Iterator<String> iterator = commands.iterator();
        int i = 0;
        while (iterator.hasNext()) {
            currentKeyboardRow.add(iterator.next());
            i++;
            if (iterator.hasNext() && i % KEYBOARD_MAX_COLUMNS == 0) {
                currentKeyboardRow = new KeyboardRow();
                keyboardRows.add(currentKeyboardRow);
            }
        }

        ReplyKeyboardMarkup replyMarkup = new ReplyKeyboardMarkup(keyboardRows);
        replyMarkup.setOneTimeKeyboard(true);
        response.setReplyMarkup(replyMarkup);
        bot.execute(response);
    }

}
