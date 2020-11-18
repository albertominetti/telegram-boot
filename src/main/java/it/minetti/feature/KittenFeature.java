package it.minetti.feature;

import com.google.common.collect.Iterables;
import it.minetti.persistence.ChatInfo;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.methods.send.SendChatAction;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import static java.util.Arrays.asList;
import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.web.util.UriComponentsBuilder.fromUriString;

@Component
public class KittenFeature implements Feature {

    public static final String ACTION_UPLOAD_PHOTO = "upload_photo";
    public static final String CAT_API = "https://api.thecatapi.com/v1/images/search?limit={limit}&size={size}";

    @Autowired
    private AbsSender bot;

    @Autowired
    private RestTemplate template;

    @Override
    public boolean test(Message message, ChatInfo chatInfo) {
        if (chatInfo.getStatus() == null && message.hasText()) {
            ResourceBundle bundle = ResourceBundle.getBundle("messages", chatInfo.getLocale());
            String text = message.getText();
            List<String> triggers = asList(bundle.getString("kitten.trigger").split(","));
            return triggers.contains(text.toLowerCase())
                    || equalsIgnoreCase(text, "/kitten")
                    || equalsIgnoreCase(text, "/cat");
        }
        return false;
    }

    @Override
    public void process(Message message, ChatInfo chatInfo) throws TelegramApiException, InterruptedException {
        ResourceBundle bundle = ResourceBundle.getBundle("messages", chatInfo.getLocale());

        String chatId = chatInfo.getChatId();

        bot.execute(new SendChatAction(chatId, ACTION_UPLOAD_PHOTO));

        try {
            ParameterizedTypeReference<List<CatSearchResult>> typeRef = new ParameterizedTypeReference<>() {
            };

            List<CatSearchResult> results = template.exchange(fromUriString(CAT_API)
                    .build(Map.of("limit", 1, "size", "med")), GET, null, typeRef).getBody();
            CatSearchResult result = Iterables.getOnlyElement(results);

            byte[] bytes = template.getForObject(result.url, byte[].class);
            bot.execute(new SendPhoto(chatId, new InputFile(new ByteArrayInputStream(bytes), "kitten")));

        } catch (Exception e) {
            bot.execute(new SendMessage(chatId, bundle.getString("kitten.error")));
        }


    }

    @Data
    public static class CatSearchResult {
        public String id;
        public String url;
    }

}
