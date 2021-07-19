package com.leonardo.arkansasproject.validators;

import com.leonardo.arkansasproject.utils.TemplateMessage;
import com.leonardo.arkansasproject.validators.exceptions.ArkansasException;

import java.util.regex.Pattern;

public class TextValidator {

    private static final Pattern pattern = Pattern.compile(
            "^(http:\\/\\/www\\.|https:\\/\\/www\\.|http:\\/\\/|https:\\/\\/)?[a-z0-9]+([\\-\\.][a-z0-9]+)*\\.[a-z]{2,5}(:[0-9]{1,5})?(\\/.*)?"
    );

    private static final Pattern patternCommand = Pattern.compile("&[A-Za-z]+");

    public static boolean isBotCommand(String text) {
        return patternCommand.matcher(text).matches();
    }

    public static void isUrlOrThrow(String text) throws ArkansasException {
        if (pattern.matcher(text).matches()) return;
        throw new ArkansasException(TemplateMessage.NOT_URL.getMessageEmbed());
    }

    public static void hasCharLenghtOrThrow(String text) throws ArkansasException {
        if (text.length() >= 6 && text.length() <= 40) return;
        throw new ArkansasException(TemplateMessage.TEXT_LENGTH_NOT_SUPPORTED.getMessageEmbed());
    }

    public static void hasCharLenghtOrThrow(String text, int min, int max) throws ArkansasException {
        if (text.length() >= min && text.length() <= max) return;
        throw new ArkansasException(TemplateMessage.TEXT_LENGTH_NOT_SUPPORTED.getMessageEmbed());
    }

    public static void hasArgsOrThrow(Object[] array, int minLenght,
                                      TemplateMessage templateMessage) throws ArkansasException {
        if (minLenght <= array.length) return;
        throw new ArkansasException(templateMessage.getMessageEmbed());
    }

    public static void hasCharLenghtOrThrow(String text, int max) throws ArkansasException {
        if (text.length() >= 6 && text.length() <= max) return;
        throw new ArkansasException(TemplateMessage.TEXT_LENGTH_NOT_SUPPORTED.getMessageEmbed());
    }

}
