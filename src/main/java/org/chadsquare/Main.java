package org.chadsquare;

import org.chadsquare.telegram_bot.*;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import redis.clients.jedis.Jedis;

public class Main {
    public static void main(String[] args) throws TelegramApiException {
        System.out.println("Hello world!");

        Jedis jedis = new Jedis("localhost", 6379);
        RedisTool redisTool = new RedisTool(jedis);

        TelegramUpdateTranslator telegramUpdateTranslator = new TelegramUpdateTranslator(new Scraper(), redisTool);
        MyBot myBot = new MyBot(telegramUpdateTranslator, new TelegramButtonBuilder(), redisTool);

        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        telegramBotsApi.registerBot(myBot);

        jedis.expire("", 300L);

    }
}