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
                            .callbackData(QueryPrefix.DYNAMIC_QUERY.value + buttonData.buttonText())
                            .build();

                    if (buttonData.isLinkButton()) {
                        inlineKeyboardButton.setUrl(buttonData.callbackData());
                    }
                    return inlineKeyboardButton;
                })
                .peek(createdButton -> System.out.println("created inline button: " + createdButton))
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

    public List<InlineKeyboardButton> createNoExternalLink(List<ButtonData> buttonDataList) {
        System.out.println("\n**************************** creating  NoJobPostingButtons for: " + buttonDataList);

        List<InlineKeyboardButton> keyboardButtonsList = new ArrayList<>();
        for (int index = 0; index <= buttonDataList.size() - 1; index++) {
            keyboardButtonsList.add(InlineKeyboardButton.builder()
                    .text(buttonDataList.get(0).buttonText())
                    .url(buttonDataList.get(0).callbackData())
                    .build());
        }
        return keyboardButtonsList;
    }

    public InlineKeyboardMarkup createInlineKeyboardMarkup(List<InlineKeyboardButton> inlineButtonList) {
        System.out.println("\n**************************** creating InlineKeyboardMarkup: " + inlineButtonList);
        System.out.println("size = " + inlineButtonList.size());

        InlineKeyboardMarkup.InlineKeyboardMarkupBuilder keyboardMarkupBuilder = InlineKeyboardMarkup.builder();
        List<InlineKeyboardButton> rowList = new ArrayList<>();

        for (int i = 0; i <= inlineButtonList.size() - 1; i++) {
            System.out.println(inlineButtonList.get(i));

            if (rowList.size() < 2) {
                System.out.println("adding to double row, index: " + i);
                System.out.println("adding to double row, index: " + inlineButtonList.get(i));
                rowList.add(inlineButtonList.get(i));
            }

            if (rowList.size() == 2 || i == inlineButtonList.size() - 1) {
                System.out.println("setting row, index: " + i);
                keyboardMarkupBuilder.keyboardRow(rowList);
                rowList = new ArrayList<>();
            }
        }

        System.out.println("build buttons: " + keyboardMarkupBuilder.build());

        return keyboardMarkupBuilder.build();
    }
}
