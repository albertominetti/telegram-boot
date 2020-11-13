package it.minetti.feature;

import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Locale;

public interface Feature {
    boolean test(Update update, String status, Locale userLocale);

    void process(Update update, String status, Locale userLocale) throws Exception;
}
