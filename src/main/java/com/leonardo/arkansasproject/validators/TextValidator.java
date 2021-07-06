package com.leonardo.arkansasproject.validators;

import java.util.regex.Pattern;

public class TextValidator {
    private static final Pattern pattern = Pattern.compile(
            "^(http:\\/\\/www\\.|https:\\/\\/www\\.|http:\\/\\/|https:\\/\\/)?[a-z0-9]+([\\-\\.][a-z0-9]+)*\\.[a-z]{2,5}(:[0-9]{1,5})?(\\/.*)?"
    );

    private static final Pattern patternCommand = Pattern.compile("&[A-Za-z]+");

    public static boolean isBotCommand(String text) {
        return patternCommand.matcher(text).matches();
    }

    public static boolean isUrl(String text) {
        return pattern.matcher(text).matches();
    }

    public static boolean characterLength(String text) {
        return text.length() >= 6 && text.length() <= 40;
    }

    public static boolean characterLength(String text, int min, int max) {
        return text.length() >= min && text.length() <= max;
    }

    public static boolean characterLength(String text, int max) {
        return text.length() >= 6 && text.length() <= max;
    }

}
