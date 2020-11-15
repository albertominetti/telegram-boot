package it.minetti.feature.entrypoint;

import it.minetti.feature.Feature;
import it.minetti.persistence.ChatInfo;
import it.minetti.persistence.ChatRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

import static java.util.Locale.ENGLISH;

@Slf4j
@Component
public class UpdateEntryPoint {

    @Autowired
    private AbsSender bot;

    @Autowired
    private List<Feature> features;

    @Autowired
    private ChatRepository repository;

    @Async
    public void process(Update update) throws TelegramApiException {
        if (!update.hasMessage()) {
            log.info("Skipping the update because has no message: {}", update);
            return;
        }

        Message message = update.getMessage();
        ChatInfo chatInfo = retrieveChatInfo(message);
        log.debug("Chat info: {}", chatInfo);


        ResourceBundle bundle = ResourceBundle.getBundle("messages", chatInfo.getLocale());

        boolean processed = false;
        for (Feature feature : features) {
            try {
                if (feature.test(message, chatInfo)) {
                    feature.process(message, chatInfo);
                    processed = true;
                    break;
                }
            } catch (Exception e) {
                log.error("Processing failed due to error", e);
                bot.execute(new SendMessage(chatInfo.getChatId(), bundle.getString("error.in.processing")));
            }
        }

        if (!processed) {
            log.warn("Message not supported: {}", update);
            SendMessage response = new SendMessage(chatInfo.getChatId(), bundle.getString("not.understand"));
            response.setReplyMarkup(new ReplyKeyboardRemove(true));
            bot.execute(response);
        }
    }

    private ChatInfo retrieveChatInfo(Message message) {
        String chatId = message.getChatId().toString();

        Optional<ChatInfo> chatInfoFomDb = repository.findById(chatId);

        if (chatInfoFomDb.isEmpty()) {
            ChatInfo chatInfo = new ChatInfo();
            chatInfo.setChatId(chatId);

            Optional<User> user = Optional.ofNullable(message.getFrom()); // can be empty when from channel
            chatInfo.setLocale(user.map(User::getLanguageCode).map(Locale::new).orElse(ENGLISH));
            chatInfo.setFirstName(user.map(User::getFirstName).orElse(null));
            chatInfo.setLastName(user.map(User::getLastName).orElse(null));
            chatInfo.setUserName(user.map(User::getUserName).orElse(null));
            chatInfo.setFirstSeen(Instant.now());

            return repository.save(chatInfo);
        } else {
            return chatInfoFomDb.get();
        }
    }

}
