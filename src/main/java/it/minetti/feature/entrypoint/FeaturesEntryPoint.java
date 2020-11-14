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

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

import static java.util.Locale.ENGLISH;
import static java.util.Optional.ofNullable;

@Slf4j
@Component
public class FeaturesEntryPoint {

    @Autowired
    private AbsSender bot;

    @Autowired
    private List<Feature> features;

    @Autowired
    private ChatRepository repository;

    @Async
    public void process(Update update) throws TelegramApiException {
        ChatInfo chatInfo = retrieveChatInfo(update);
        log.debug("Chat info: {}", chatInfo);

        boolean processed = false;
        for (Feature feature : features) {
            try {
                if (feature.test(update, chatInfo.getStatus(), chatInfo.getLocale())) {
                    feature.process(update, null, chatInfo.getLocale());
                    processed = true;
                    break;
                }
            } catch (Exception e) {
                log.error("Failed due to error", e);
            }
        }

        if (!processed && chatInfo.getChatId() != null) {
            log.warn("Message not supported: {}", update);
            ResourceBundle bundle = ResourceBundle.getBundle("messages", chatInfo.getLocale());
            SendMessage response = new SendMessage(chatInfo.getChatId(), bundle.getString("not.understand"));
            response.setReplyMarkup(new ReplyKeyboardRemove(true));
            bot.execute(response);
        } else {
            log.error("Impossible to process the message: {}", update);
        }

    }

    private ChatInfo retrieveChatInfo(Update update) {
        Optional<String> chatId = ofNullable(update.getMessage())
                .or(() -> ofNullable(update.getEditedMessage()))
                .map(Message::getChatId).map(Object::toString);

        ChatInfo chatInfo = chatId
                .flatMap(id -> repository.findById(id))
                .orElse(null);

        if (chatInfo == null) {
            chatInfo = new ChatInfo();
            chatInfo.setChatId(chatId.orElse(null));
            chatInfo.setLocale(ofNullable(update.getMessage()).map(Message::getFrom)
                    .map(User::getLanguageCode).map(Locale::new).orElse(ENGLISH));

            if (chatId.isPresent()) {
                chatInfo = repository.save(chatInfo);
            }
        }
        return chatInfo;
    }

}
