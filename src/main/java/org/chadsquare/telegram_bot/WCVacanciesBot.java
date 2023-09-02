package org.chadsquare.telegram_bot;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class WCVacanciesBot extends TelegramLongPollingBot {

    private final MessageTranslator translator;

    public WCVacanciesBot(String botToken, MessageTranslator translator) {
        super(botToken);
        this.translator = translator;
    }

    @Override
    public String getBotUsername() {
        return "WestVacs";
    }

    @Override
    public void onUpdateReceived(Update update) {
        System.out.println("Incoming update received: " + update);

        if (update.getMessage() != null && !update.getMessage().getText().startsWith("/")) {
            //  normal text message and KeyboardButton replies will come through in the message property
            System.out.println("normal user text msg received: " + update.getMessage().getText());

            SendMessage sendMessage1 = this.translator.setupMessage(update.getMessage().getChatId().toString(),
                    "Welcome to your Telegram bot.\n\nSelect one of the menu commands to get started.");
            sendMessage(sendMessage1);

        } else if (update.getMessage() != null && update.getMessage().getText().startsWith("/")) {
            //  Handle menu commands replies and start message.

            String command = update.getMessage().getText();
            String chatId = update.getMessage().getChatId().toString();
            System.out.println("translating command: " + command);

            SendMessage sendMessage = this.translator.translateMenuCommand(update, command, chatId);
            sendMessage(sendMessage);

        } else  if (update.getCallbackQuery() != null) {
            //  handle inline keyboard replies
            System.out.println("handling CallbackQuery: " + update.getCallbackQuery());

            CallbackQuery callbackQuery = update.getCallbackQuery();
            String chatId = callbackQuery.getMessage().getChatId().toString();
            String query = callbackQuery.getData();

            SendMessage sendMessage = this.translator.translateMenuCommand(update, query, chatId);
            sendMessage(sendMessage);

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

}
