package org.chadsquare.telegram_bot;

import org.chadsquare.scraper.Scraper;
import org.jsoup.select.Elements;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MessageTranslator {

    private static final String DEPARTMENTS_CSS_SELECTOR = ".inside #node-wcg-block-52449 .field-item ul li a";
    private static final String WC_VACANCIES_URL = "https://www.westerncape.gov.za/jobs/";

    private final Scraper scraper;

    private final RedisTool redisTool;

    private final TelegramButtonBuilder buttonBuilder;

    public MessageTranslator(Scraper scraper, RedisTool redisTool, TelegramButtonBuilder buttonBuilder) {
        this.redisTool = redisTool;
        this.scraper = scraper;
        this.buttonBuilder = buttonBuilder;
    }

    public SendMessage translateMenuCommand(Update update, String commandQuery, String chatId) {

        switch (commandQuery) {
            case "/start" -> {
                System.out.println("incoming message matched /start");

                    return setupMessage(update.getMessage().getChatId().toString(),
                            "Welcome to your Telegram bot.\n\nSelect one of the menu commands to get started.");
            }
            case "/departments" -> {
                System.out.println("incoming message matched /departments");

                Map<String, String> departmentUrlByChatId = this.redisTool.getAllDepartmentUrlsByChatId(chatId);

                if (departmentUrlByChatId.isEmpty()) {
                    System.out.println("scraping new data for chatID ...");

                    Elements elements = this.scraper.scrapUrlAndExtractElements(WC_VACANCIES_URL, DEPARTMENTS_CSS_SELECTOR);
                    List<ButtonData> departmentButtonData = this.createDepartmentButtonData(elements);
                    this.redisTool.cacheDepartmentUrls(departmentButtonData, chatId);
                    System.out.println(departmentButtonData);

                    List<InlineKeyboardButton> departmentButtons = this.buttonBuilder.createDepartmentButtons(departmentButtonData);
                    InlineKeyboardMarkup inlineKeyboardMarkup = this.buttonBuilder.createInlineKeyboardMarkup(departmentButtons);
                    return this.setupMessage(chatId, "Available departments:\n\n", inlineKeyboardMarkup);

                } else {
                    System.out.println("found redis cached data for chatID ...");

                    List<ButtonData> cachedButtonDataList = new ArrayList<>();
                    departmentUrlByChatId.forEach((departmentName, url) -> {
                        ButtonData buttonData = new ButtonData(departmentName, url);
                        cachedButtonDataList.add(buttonData);
                    });

                    List<InlineKeyboardButton> departmentButtons = this.buttonBuilder.createDepartmentButtons(cachedButtonDataList);
                    InlineKeyboardMarkup inlineKeyboardMarkup = this.buttonBuilder.createInlineKeyboardMarkup(departmentButtons);
                    return this.setupMessage(chatId, "Available departments:\n\n", inlineKeyboardMarkup);
                }
            }
            case "Agriculture", "Cultural Affairs and Sport", "Economic Development and Tourism",
                    "Environmental Affairs and Development Planning", "Infrastructure", "Local Government",
                    "Mobility", "Police Oversight and Community Safety", "Premier",
                    "Provincial Treasury", "Social Development", "Human Settlements" -> {
                System.out.printf("incoming message matched a department: %s\n", commandQuery);
                System.out.println("scraping for vacancies...");

                String urlToScrape = this.redisTool.getDepartmentUrlByDepartmentAndChatId(commandQuery, chatId);
                String cssSelector = "#udpSearchResults .content-panel .list tbody tr";

                Elements elements = this.scraper.scrapUrlAndExtractElements(urlToScrape, cssSelector);

                if (!elements.isEmpty()) {

                    List<ButtonData> departmentButtonData = this.createVacanciesButtonData(elements);
                    this.redisTool.cacheDepartmentUrls(departmentButtonData, chatId);
                    System.out.println(departmentButtonData);
                    List<InlineKeyboardButton> jobPostingButtons = this.buttonBuilder.createJobPostingButtons(departmentButtonData);
                    InlineKeyboardMarkup inlineKeyboardMarkup = this.buttonBuilder.createInlineKeyboardMarkup(jobPostingButtons);

                    StringBuilder heading = new StringBuilder()
                            .append("Vacancies for the ")
                            .append(commandQuery)
                            .append(" department:\n\n");

                    for (int index = 0; index <= departmentButtonData.size() - 1; index++) {
                        heading.append("""
                        Option %s:
                        %s

                        """.formatted((index + 1), departmentButtonData.get(index).buttonText()));
                    }

                    return this.setupMessage(chatId, heading.toString(), inlineKeyboardMarkup);

                } else {
                    System.out.printf("No vacancies available for the selected department: %s\n", commandQuery);
                    return this.setupMessage(chatId, "No vacancies available for the selected department");
                }
            }
            case "Education" -> {
                System.out.printf("incoming message matched a department: %s\n", commandQuery);

                String departmentUrlByDepartmentAndChatId = this.redisTool.getDepartmentUrlByDepartmentAndChatId("Education", chatId);
                List<ButtonData> buttonDataList = List.of(new ButtonData("Education vacancies", departmentUrlByDepartmentAndChatId));

                List<InlineKeyboardButton> jobPostingButtons = this.buttonBuilder.createJobPostingButtons(buttonDataList);
                InlineKeyboardMarkup inlineKeyboardMarkup = this.buttonBuilder.createInlineKeyboardMarkup(jobPostingButtons);
                return this.setupMessage(chatId, "Vacancies for " + commandQuery, inlineKeyboardMarkup);

            }
            case "Health and Wellness" -> {
                System.out.printf("incoming message matched a department: %s\n", commandQuery);

                String departmentUrlByDepartmentAndChatId = this.redisTool.getDepartmentUrlByDepartmentAndChatId("Education", chatId);
                List<ButtonData> buttonDataList = List.of(new ButtonData("Health and Wellness", departmentUrlByDepartmentAndChatId));

                List<InlineKeyboardButton> jobPostingButtons = this.buttonBuilder.createJobPostingButtons(buttonDataList);
                InlineKeyboardMarkup inlineKeyboardMarkup = this.buttonBuilder.createInlineKeyboardMarkup(jobPostingButtons);
                return this.setupMessage(chatId, "Vacancies for " + commandQuery, inlineKeyboardMarkup);
            }
            default -> {
                System.out.printf("no match for incoming message: %s\n", commandQuery);
                return null;
            }
        }
    }

    public List<ButtonData> createDepartmentButtonData(Elements elements) {
        ArrayList<ButtonData> buttonDataList = new ArrayList<>();

        elements.forEach((element) -> {
            ButtonData buttonData = new ButtonData(element.ownText(), element.attr("href"));
            buttonDataList.add(buttonData);
        });
        return buttonDataList;
    }

    public List<ButtonData> createVacanciesButtonData(Elements elements) {
        List<ButtonData> buttonDataList = new ArrayList<>();

            elements.forEach((element) -> {
                String onclickString = element.attr("onclick");
                String onclickUrl = onclickString.substring(onclickString.indexOf("'"), onclickString.length() - 1)
                        .replaceAll("'", "");
                ButtonData buttonData = new ButtonData(element.text(), "https://westerncapegov.erecruit.co" + onclickUrl);
                buttonDataList.add(buttonData);
            });


        return buttonDataList;
    }

    public SendMessage setupMessage(String chatId, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(text);
        return sendMessage;
    }

    private SendMessage setupMessage(String chatId, String text, ReplyKeyboard replyKeyboardMarkup) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(text);
        sendMessage.setParseMode("HTML");
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        return sendMessage;
    }

}
