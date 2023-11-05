package org.chadsquare;

import org.chadsquare.telegram_bot.MessageTranslator;
import org.chadsquare.telegram_bot.WCVacanciesBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class Main {

    private static final String BOT_TOKEN = "<botToken>";
    public static void main(String[] args) throws TelegramApiException {
        System.out.println("Starting western-cape-vacancies-telegram-bot...\n");

        MessageTranslator messageTranslator = new MessageTranslator("localhost", 6379);
        WCVacanciesBot WCVacanciesBot = new WCVacanciesBot(BOT_TOKEN, messageTranslator);

        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        telegramBotsApi.registerBot(WCVacanciesBot);

    }
}