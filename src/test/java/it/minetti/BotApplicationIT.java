package it.minetti;

import it.minetti.persistence.ChatRepository;
import it.minetti.pollingbot.PollingBotConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockBeans;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import static com.google.common.collect.Sets.newHashSet;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.http.HttpStatus.FOUND;
import static org.springframework.http.HttpStatus.OK;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
        "BOT_TOKEN=123456",
        "BOT_NAME=for_test_bot"
})
@EnableAutoConfiguration(exclude = DataSourceAutoConfiguration.class)
@ContextConfiguration(classes = BotApplicationIT.SmokeConfig.class)
@MockBeans({
        @MockBean(PollingBotConfig.class),
        @MockBean(ChatRepository.class)
})
class BotApplicationIT {

    @Autowired
    TestRestTemplate template;

    @LocalServerPort
    private int port;

    @Test
    void contextLoad() {
        ResponseEntity<String> entity = template.getForEntity("http://localhost:{port}/", String.class, port);
        assertThat(entity, is(not(nullValue())));
        assertThat(entity.getStatusCode(), is(in(newHashSet(OK, FOUND))));
    }

    @TestConfiguration
    public static class SmokeConfig {
        @Bean
        public TestRestTemplate testRestTemplate() {
            return new TestRestTemplate();
        }
    }
}