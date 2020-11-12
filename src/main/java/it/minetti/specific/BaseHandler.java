package it.minetti.specific;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface BaseHandler {
    boolean test(Update update, String status);

    void process(Update update, String status) throws Exception;
}
