package org.chadsquare.telegram_bot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

import static java.util.Objects.isNull;

public class MyBot extends TelegramLongPollingBot {

    private static final Logger LOG = LoggerFactory.getLogger(MyBot.class);
    private final TelegramUpdateTranslator translator;
    private final TelegramButtonBuilder buttonBuilder;
    private final RedisTool redisTool;

    public MyBot(TelegramUpdateTranslator translator, TelegramButtonBuilder buttonBuilder, RedisTool redisTool) {
        super("6605815470:AAFB7BqfPH4LEchXJoNCkRzrP8geG2jKXd0");
        this.translator = translator;
        this.buttonBuilder = buttonBuilder;
        this.redisTool = redisTool;
    }

    @Override
    public String getBotUsername() {
        return "WestyVac";
    }

    @Override
    public void onUpdateReceived(Update update) {
        System.out.println("Incoming update received: " + update);

//        SendMessage sendMessage = new SendMessage(update.getMessage().getChatId().toString(), "hi from bot");

        //  normal text message and KeyboardButton replies will come through in the message property
        if (update.getMessage() != null && !update.getMessage().getText().startsWith("/")) {
            //  normal text
            System.out.println("normal user text msg received: " + update.getMessage().getText());

            SendMessage sendMessage = new SendMessage();
            setupMessage(update.getMessage().getChatId().toString(),
                    "Welcome to your Telegram bot.\n\nSelect one of the menu commands to get started.", sendMessage);
            sendMessage(sendMessage);
            return;

        } else if (update.getMessage() != null && update.getMessage().getText().startsWith("/")) {
            //  Handle menu commands replies and start message.

            System.out.println();
            List<ButtonData> buttonDataList = this.translator.translateMenuCommand(update);
            if (!isNull(buttonDataList)) {
                List<InlineKeyboardButton> inlineKeyboardButtons = this.buttonBuilder.createDepartmentButtons(buttonDataList);
                InlineKeyboardMarkup inlineKeyboardMarkup = this.buttonBuilder.createInlineKeyboardMarkup(inlineKeyboardButtons);
                System.out.println("about to send");
                System.out.println(inlineKeyboardMarkup);

                SendMessage sendMessage = new SendMessage();
                setupMessage(update.getMessage().getChatId().toString(),
                        "Choose from departments below", sendMessage, inlineKeyboardMarkup);
                sendMessage(sendMessage);
                return;
            } else {
                //  start message
                System.out.println("Start message received: " + update.getMessage().getText());

                SendMessage sendMessage = new SendMessage();
                setupMessage(update.getMessage().getChatId().toString(),
                        "Welcome to your Telegram bot.\n\nSelect one of the menu commands to get started.",sendMessage);
                sendMessage(sendMessage);
                return;
            }


            //  handle inline keyboard replies
        } else  if (update.getCallbackQuery() != null) {
            System.out.println("handling CallbackQuery: " + update.getCallbackQuery());

            List<ButtonData> buttonDataList = this.translator.translateQuery(update);
            System.out.println("done with query");
            if (buttonDataList != null) {
                if (buttonDataList.size() == 1) {
                    if (buttonDataList.get(0).isLinkButton()) {
                        List<InlineKeyboardButton> noJobPostingButtons = this.buttonBuilder.createNoExternalLink(buttonDataList);
                        InlineKeyboardMarkup inlineKeyboardMarkup = this.buttonBuilder.createInlineKeyboardMarkup(noJobPostingButtons);

                        SendMessage sendMessage = new SendMessage();
                        setupMessage(update.getCallbackQuery().getMessage().getChatId().toString(),
                                buttonDataList.get(0).buttonText(), sendMessage, inlineKeyboardMarkup);
                        sendMessage(sendMessage);
                        return;
                    } else {
                        SendMessage sendMessage = new SendMessage();
                        setupMessage(update.getCallbackQuery().getMessage().getChatId().toString(),
                                buttonDataList.get(0).buttonText(), sendMessage);
                        sendMessage(sendMessage);
                        return;
                    }
                }
                if (buttonDataList.size() > 1) {
                    List<InlineKeyboardButton> jobPostingButtons = this.buttonBuilder.createJobPostingButtons(buttonDataList);
                    InlineKeyboardMarkup inlineKeyboardMarkup = this.buttonBuilder.createInlineKeyboardMarkup(jobPostingButtons);

                    StringBuilder optionDescriptions = new StringBuilder();

                    for (int index = 0; index <= buttonDataList.size() - 1; index++) {
                        optionDescriptions.append("""
                        Option %s:
                        %s

                        """.formatted((index + 1), buttonDataList.get(index).buttonText()));
                    }

                    SendMessage sendMessage = new SendMessage();
                    setupMessage(update.getCallbackQuery().getMessage().getChatId().toString(),
                            optionDescriptions.toString(), sendMessage, inlineKeyboardMarkup);
                    sendMessage(sendMessage);
                    return;
                }

            } else {
                SendMessage sendMessage = new SendMessage();
                setupMessage(update.getCallbackQuery().getMessage().getChatId().toString(),
                        "unknown command.\nSelect one of the menu commands to get started. ", sendMessage);
                sendMessage(sendMessage);
                return;
            }


        }
    }

    public void sendMessage(SendMessage sendMessage) {
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            System.out.println(e);
            throw new RuntimeException(e);
        }
    }

    private static void setupMessage(String chatId, String text, SendMessage sendMessage, ReplyKeyboard replyKeyboardMarkup) {
        sendMessage.setChatId(chatId);
        sendMessage.setText(text);
        sendMessage.setParseMode("HTML");
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
    }

    private static void setupMessage(String chatId, String text, SendMessage sendMessage, ReplyKeyboard replyKeyboardMarkup, List<ButtonData> buttonDataList) {
        sendMessage.setChatId(chatId);
        sendMessage.setText(text);
        sendMessage.setParseMode("HTML");
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
    }

    private static void setupMessage(String chatId, String text, SendMessage sendMessage) {
        sendMessage.setChatId(chatId);
        sendMessage.setText(text);
    }

}
