package org.chadsquare.telegram_bot;

import org.chadsquare.scraper.Scraper;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class TelegramUpdateTranslator {

    private static final String DEPARTMENTS_URL = ".inside #node-wcg-block-52449 .field-item ul li a";
    private static final String WC_VACANCIES_URL = "https://www.westerncape.gov.za/jobs/";

    public static final int DYNAMIC_QUERY_LENGTH = 15;

    private Scraper scraper;

    private RedisTool redisTool;

    public TelegramUpdateTranslator(Scraper scraper, RedisTool redisTool) {
        this.redisTool = redisTool;
        this.scraper = scraper;
    }

//    public List<ButtonData> translateCommand(Update update) {
//        String command = update.getMessage().getText();
//
//        switch (command) {
//            case "/start":
////                setupMessage(update.getMessage().getChatId().toString(),
////                        "Welcome to your Telegram bot.\n\nSelect one of the menu commands to get started.", sendMessage);
//                // scrape url
//                // return inline buttons
////                return new Object();
//                return null;
//                break;
//
//            case "/departments":
//                //  create inline buttons
////                InlineKeyboardMarkup inlineKeyboardMarkup = createInlineKeyboardMarkup();
////                setupMessage(update.getMessage().getChatId().toString(), "Example of InlineKeyboard buttons",
////                        sendMessage, inlineKeyboardMarkup);
//                String url = "https://www.westerncape.gov.za/jobs/";
//                String cssSelector = ".inside .panel-pane .pane-content";
//                ArrayList<ButtonData> buttonDataList = getButtonData(url, cssSelector);
//
//                return buttonDataList;
//            break;
//
//            case "/selected-department":
//                //  create inline buttons
////                InlineKeyboardMarkup inlineKeyboardMarkup = createInlineKeyboardMarkup();
////                setupMessage(update.getMessage().getChatId().toString(), "Example of InlineKeyboard buttons",
////                        sendMessage, inlineKeyboardMarkup);
//
////                String url = "https://www.westerncape.gov.za/jobs/";
////                String cssSelector = ".inside .panel-pane .pane-content";
////                ArrayList<ButtonData> buttonDataList = getButtonData(url, cssSelector);
////
////                return buttonDataList;
//            break;
//
//            case "/reply":
//                //  create predefined reply buttons
////                ReplyKeyboardMarkup replyKeyboardMarkup = createReplyKeyboardMarkup();
////                setupMessage(update.getMessage().getChatId().toString(), "Example of ReplyKeyboard buttons",
////                        sendMessage, replyKeyboardMarkup);
////                return new Object();
//                Document scrapped = this.scraper.scrap("https://www.westerncape.gov.za/jobs/x235");
//                return null;
//                break;
//
//            default:
//                System.out.println("unknown command: " + command);
//                return null;
//        }
//    }
    public List<ButtonData> translateMenuCommand(Update update) {
            String command = update.getMessage().getText();
            String chatId = update.getMessage().getChatId().toString();
        System.out.println("translating command: " + command);

        switch (command) {
            case "/start" -> {
//                    setupMessage(update.getMessage().getChatId().toString(),
//                            "Welcome to your Telegram bot.\n\nSelect one of the menu commands to get started.", sendMessage);
                return null;
            }
            case "/departments" -> {
                System.out.println("matched /departments");
                Map<String, String> departmentUrlByChatId = this.redisTool.getAllDepartmentUrlsByChatId(chatId);
                Boolean isLinkButton = false;

                if (departmentUrlByChatId.isEmpty()) {
                    System.out.println("scraping new data...");
//                    String url = "https://www.westerncape.gov.za/jobs/";
//                    String cssSelector = ".inside #node-wcg-block-52449 .field-item ul li a";
                    ArrayList<ButtonData> buttonDataList = getButtonData(WC_VACANCIES_URL, DEPARTMENTS_URL, isLinkButton);
                    this.redisTool.cacheDepartmentUrls(buttonDataList, chatId);
                    System.out.println(buttonDataList);
                    return buttonDataList;
                } else {
                    System.out.println("redis found cached urls!!!");
                    System.out.println(departmentUrlByChatId);

                    List<ButtonData> buttonDataList = new ArrayList<>();
                    departmentUrlByChatId.forEach((departmentName, url) -> {
                        ButtonData buttonData = new ButtonData(departmentName, url, isLinkButton);
                        buttonDataList.add(buttonData);
                    });
                    return buttonDataList;
                }

            }
            default -> {
                System.out.println("unknown command: " + command);
                return null;
            }
        }
    }



    public List<ButtonData> translateQuery(Update update) {
        System.out.println("**sdfsdf");
        CallbackQuery callbackQuery = update.getCallbackQuery();
        System.out.println("callbackQuery: " + callbackQuery);
        String chatId = callbackQuery.getMessage().getChatId().toString();
        System.out.println("chatId: " + chatId);
        String callbackData = callbackQuery.getData();
        System.out.println("callbackData: " + callbackData);
        System.out.println("callbackData received: " + callbackData);

        if (callbackData.startsWith(QueryPrefix.DYNAMIC_QUERY.value)) {
            System.out.println("received a dynamic query...");
            String query = callbackData.substring(DYNAMIC_QUERY_LENGTH);

            switch (query) {
                case "Agriculture", "Cultural Affairs and Sport", "Economic Development and Tourism",
                        "Environmental Affairs & Development Planning", "Infrastructure", "Local Government",
                        "Mobility", "Police Oversight and Community Safety", "Premier",
                        "Provincial Treasury", "Social Development", "Human Settlements" -> {
                    System.out.println("scraping new data for department vacancies...");

                    String urlToScrape = this.redisTool.getDepartmentUrlByDepartmentAndChatId(query, chatId);
                    String cssSelector = "#udpSearchResults .content-panel .list tbody tr";

                    List<ButtonData> buttonDataList = scrapWesterncapegovSite(urlToScrape, cssSelector, true);
                    System.out.println(buttonDataList);
                    return buttonDataList;
                }
                case "Education"-> {
                    System.out.println("returning Education link...");

                    String departmentUrlByDepartmentAndChatId = this.redisTool.getDepartmentUrlByDepartmentAndChatId("Education", chatId);
                    return List.of(new ButtonData("Education vacancies", departmentUrlByDepartmentAndChatId, true));
                }
                case "Health and Wellness" -> {
                    System.out.println("returning Health and Wellness link...");

                    String departmentUrlByDepartmentAndChatId = this.redisTool.getDepartmentUrlByDepartmentAndChatId("Education", chatId);
                    return List.of(new ButtonData("Health and Wellness", departmentUrlByDepartmentAndChatId, true));
                }
                default -> {
                    System.out.println("unknown command: " + callbackData);
                    return null;
                }
            }
        } else {
            System.out.println("unknown command: " + callbackData);
            return null;
        }
//        switch (callbackData) {
//            case "query-1" -> {
//                //  create inline buttons
////                InlineKeyboardMarkup inlineKeyboardMarkup = createInlineKeyboardMarkup();
////                setupMessage(update.getMessage().getChatId().toString(), "Example of InlineKeyboard buttons",
////                        sendMessage, inlineKeyboardMarkup);
//                String url = "https://www.westerncape.gov.za/jobs/";
//                String cssSelector = ".inside .panel-pane .pane-content";
//                ArrayList<ButtonData> buttonDataList = getButtonData(url, cssSelector, false);
//                return buttonDataList;
//            }
//            default -> {
//                System.out.println("unknown command: " + callbackData);
//                return null;
//            }
//        }

    }

    private ArrayList<ButtonData> getButtonData(String url, String cssSelector, Boolean isLinkButton) {
        Optional<Document> optionalDocument = Optional.ofNullable(this.scraper.scrap(url));
        ArrayList<ButtonData> buttonDataList = new ArrayList<>();

        optionalDocument.ifPresentOrElse((document -> {
                Elements elements = scraper.selectSimpleElement(document, cssSelector);
                elements.forEach((element) -> {
                    System.out.println("found element " + element);
                    ButtonData buttonData = new ButtonData(element.ownText(), element.attr("href"), isLinkButton);
                    buttonDataList.add(buttonData);
                });
            }),
            () -> System.out.println("Document could not be found"));
        System.out.println("found data for buttons: " + buttonDataList);
        return buttonDataList;
    }

    private List<ButtonData> scrapWesterncapegovSite(String url, String cssSelector, Boolean isLinkButton) {
        Optional<Document> optionalDocument = Optional.ofNullable(this.scraper.scrap(url));
        ArrayList<ButtonData> buttonDataList = new ArrayList<>();

        optionalDocument.ifPresentOrElse((document -> {
                    Elements elements = scraper.selectSimpleElement(document, cssSelector);
                    if (!elements.isEmpty()) {
                        elements.forEach((element) -> {
                            String onclickString = element.attr("onclick");
                            String onclickUrl = onclickString.substring(onclickString.indexOf("'"), onclickString.length() - 1)
                                    .replaceAll("'", "");
                            ButtonData buttonData = new ButtonData(element.text(), "https://westerncapegov.erecruit.co" + onclickUrl, isLinkButton);
                            buttonDataList.add(buttonData);
                        });
                    } else {
                        ButtonData buttonData = new ButtonData("No vacancies found for the selected department", "", false);
                        buttonDataList.add(buttonData);
                    }
                }),
                () -> System.out.println("Document could not be found"));
        System.out.println("found data for buttons: " + buttonDataList);
        return buttonDataList;
    }

    public void scrap(String url) {
        Document scrapped = this.scraper.scrap("https://www.westerncape.gov.za/jobs/" + "departmenmtName");
        scraper.selectSimpleElement(scrapped, ".inside .panel-pane .pane-content");
    }
}
