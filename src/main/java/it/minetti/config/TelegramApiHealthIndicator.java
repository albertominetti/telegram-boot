package it.minetti.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import static org.telegram.telegrambots.meta.ApiConstants.BASE_URL;

@Slf4j
@Component
public class TelegramApiHealthIndicator implements HealthIndicator {

    @Value("${bot.token}")
    private String botToken;

    @Autowired
    RestTemplate template;

    @Override
    public Health health() {

        try {
            OkResponse ok = template.getForObject(BASE_URL + botToken + "/getMe", OkResponse.class);
            return (ok != null && ok.isOk())
                    ? Health.up().build()
                    : Health.outOfService().withDetail("error", ok).build();
        } catch (Exception e) {
            log.warn("Failed to connect to: {}", BASE_URL);
            return Health.down()
                    .withDetail("error", e.getMessage())
                    .build();
        }
    }

    @Data
    public static class OkResponse {
        private boolean ok;
    }
}