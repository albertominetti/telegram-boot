package it.minetti.feature;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface Feature {
    boolean test(Update update, String status);

    void process(Update update, String status) throws Exception;
}
