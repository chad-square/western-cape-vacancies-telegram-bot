package org.chadsquare.cache;

import org.chadsquare.telegram_bot.ButtonData;
import redis.clients.jedis.Jedis;

import java.util.List;
import java.util.Map;

public class RedisTool {

    private final Jedis jedisClient;

    public RedisTool(Jedis jedisClient) {
        this.jedisClient = jedisClient;

    }


    public void cacheDepartmentUrls(List<ButtonData> buttonDataList, String chatId) {
        String key = constructDepartmentKey(chatId);
        buttonDataList.forEach(buttonData -> {
            jedisClient.hset(key, buttonData.buttonText(), buttonData.callbackData());
        });

        //  only store data for 5 minutes
        jedisClient.expire(key, 300L);
    }

    public String getDepartmentUrlByDepartmentAndChatId(String departmentName, String chatId) {
        System.out.printf("Attempting to get a department Url  for Department:%s and ChatId: %s\n", departmentName, chatId);
        String url = jedisClient.hget(constructDepartmentKey(chatId), departmentName);
        System.out.printf("found Department Url Department: %s And ChatId: %s found url: %s \n", departmentName, chatId, url);
        return url;
    }

    public Map<String, String> getAllDepartmentUrlsByChatId(String chatId) {
        System.out.println("Attempting to get All department urls for chatId: " + chatId);
        Map<String, String> departmentUrlMap = jedisClient.hgetAll(chatId);
        System.out.printf("found getDepartmentUrlsByChatId chatId: %s\n url: %s\n", chatId, departmentUrlMap);

        return departmentUrlMap;
    }

    private String constructDepartmentKey(String chatId) {
        return chatId + "department";
    }


}
