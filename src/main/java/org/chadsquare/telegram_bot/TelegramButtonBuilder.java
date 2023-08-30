package org.chadsquare.telegram_bot;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

public class TelegramButtonBuilder {

    public List<InlineKeyboardButton> createDepartmentButtons(List<ButtonData> buttonDataList) {
        System.out.println("\n**************************** creating InlineKeyboardButtons: " + buttonDataList);
        return buttonDataList.stream()
                .map(buttonData -> {

                    InlineKeyboardButton inlineKeyboardButton = InlineKeyboardButton.builder()
                            .text(buttonData.buttonText())
                            .callbackData(buttonData.buttonText())
                            .build();

                    return inlineKeyboardButton;
                })
                .toList();
    }

    public List<InlineKeyboardButton> createJobPostingButtons(List<ButtonData> buttonDataList) {
        System.out.println("\n**************************** creating createJobPostingButtons for: " + buttonDataList);

        List<InlineKeyboardButton> keyboardButtonsList = new ArrayList<>();
        for (int index = 0; index <= buttonDataList.size() - 1; index++) {
            keyboardButtonsList.add(InlineKeyboardButton.builder()
                    .text("option " + (index + 1))
                    .url(buttonDataList.get(index).callbackData())
                    .build());
        }
        return keyboardButtonsList;
    }

    public InlineKeyboardMarkup createInlineKeyboardMarkup(List<InlineKeyboardButton> inlineButtonList) {
        System.out.println("\n**************************** creating InlineKeyboardMarkup: " + inlineButtonList);

        InlineKeyboardMarkup.InlineKeyboardMarkupBuilder keyboardMarkupBuilder = InlineKeyboardMarkup.builder();
        List<InlineKeyboardButton> rowList = new ArrayList<>();

        for (int i = 0; i <= inlineButtonList.size() - 1; i++) {

            if (rowList.size() < 2) {
                rowList.add(inlineButtonList.get(i));
            }

            if (rowList.size() == 2 || i == inlineButtonList.size() - 1) {
                keyboardMarkupBuilder.keyboardRow(rowList);
                rowList = new ArrayList<>();
            }
        }

        System.out.println("build buttons: " + keyboardMarkupBuilder.build());
        return keyboardMarkupBuilder.build();
    }
}
