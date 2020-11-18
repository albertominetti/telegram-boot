package it.minetti.feature;

import it.minetti.persistence.ChatInfo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.methods.send.SendChatAction;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Locale;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.atMostOnce;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class KittenFeatureIT {

    @Mock
    private AbsSender bot;

    @Spy
    private final RestTemplate restTemplate = new RestTemplate();

    @InjectMocks
    KittenFeature feature;

    @Captor
    private ArgumentCaptor<SendPhoto> captor;

    @Test
    void testActualDownload() throws IOException, InterruptedException, TelegramApiException {
        // when
        feature.process(new Message(), aChatInfo());

        //then
        verify(bot, atMostOnce()).execute(new SendChatAction("1234", "upload_photo"));

        verify(bot).execute(captor.capture());
        SendPhoto capturedChatInfo = this.captor.getValue();
        assertThat(capturedChatInfo, is(not(nullValue())));
        assertThat(capturedChatInfo.getChatId(), is("1234"));
        InputFile capturedPhoto = capturedChatInfo.getPhoto();
        assertThat(capturedPhoto, is(not(nullValue())));
        InputStream newMediaStream = capturedPhoto.getNewMediaStream();
        assertThat(newMediaStream, is(not(nullValue())));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        org.apache.commons.io.IOUtils.copy(newMediaStream, baos);
        ByteArrayInputStream photoStream = new ByteArrayInputStream(baos.toByteArray());


        String contentType = URLConnection.guessContentTypeFromStream(photoStream);
        assertThat(contentType, startsWith("image/"));

        Files.copy(photoStream, Paths.get("target", "kitten.jpeg"), REPLACE_EXISTING); // for manual inspection
    }

    private ChatInfo aChatInfo() {
        return new ChatInfo() {{
            setChatId("1234");
            setLocale(new Locale("it"));
        }};
    }
}