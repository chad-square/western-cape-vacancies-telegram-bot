package org.chadsquare;

import org.chadsquare.telegram_bot.MessageTranslator;
import org.chadsquare.telegram_bot.WCVacanciesBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class Main {
    public static void main(String[] args) throws TelegramApiException {
        System.out.println("Starting western-cape-vacancies-telegram-bot...\n");

        MessageTranslator messageTranslator = new MessageTranslator("localhost", 6379);

        WCVacanciesBot WCVacanciesBot =
                new WCVacanciesBot("6369955728:AAFwqehx712V3FaLpKREpqLlMoDYhWZSC-0", messageTranslator);

        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        telegramBotsApi.registerBot(WCVacanciesBot);

    }
}