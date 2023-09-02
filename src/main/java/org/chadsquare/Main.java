package org.chadsquare;

import org.chadsquare.cache.RedisTool;
import org.chadsquare.scraper.Scraper;
import org.chadsquare.telegram_bot.MessageTranslator;
import org.chadsquare.telegram_bot.TelegramButtonBuilder;
import org.chadsquare.telegram_bot.WCVacanciesBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import redis.clients.jedis.Jedis;

public class Main {
    public static void main(String[] args) throws TelegramApiException {
        System.out.println("Starting western-cape-vacancies-telegram-bot...\n");

        Jedis jedis = new Jedis("localhost", 6379);

        MessageTranslator messageTranslator =
                new MessageTranslator(new Scraper(),  new RedisTool(jedis), new TelegramButtonBuilder());

        WCVacanciesBot WCVacanciesBot =
                new WCVacanciesBot("6605815470:AAFB7BqfPH4LEchXJoNCkRzrP8geG2jKXd0", messageTranslator);

        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        telegramBotsApi.registerBot(WCVacanciesBot);

    }
}