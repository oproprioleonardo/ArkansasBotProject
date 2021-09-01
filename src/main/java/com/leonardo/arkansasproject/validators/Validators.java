package com.leonardo.arkansasproject.validators;

import com.leonardo.arkansasproject.utils.BotConfig;
import com.leonardo.arkansasproject.utils.TemplateMessage;
import com.leonardo.arkansasproject.validators.exceptions.ArkansasException;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Objects;
import java.util.regex.Pattern;

public class Validators {

    private static final Pattern pattern = Pattern.compile(
            "^(http:\\/\\/www\\.|https:\\/\\/www\\.|http:\\/\\/|https:\\/\\/)?[a-z0-9]+([\\-\\.][a-z0-9]+)*\\.[a-z]{2,5}(:[0-9]{1,5})?(\\/.*)?"
    );

    private static final Pattern patternCommand = Pattern.compile("&[A-Za-z]+");

    public static boolean isBotCommand(String text) {
        return patternCommand.matcher(text).matches();
    }

    public static void isAdmin(MessageReceivedEvent mre, BotConfig botConfig) throws ArkansasException {
        final Boolean[] var = new Boolean[]{false};
        final User user = mre.getAuthor();
        if (mre.isFromGuild()) {
            final Guild guild = mre.getGuild();
            final Member member = Objects.requireNonNull(guild).retrieveMember(user).complete();
            botConfig.getConfig().getAsJsonArray("admin").deepCopy()
                     .forEach(element -> var[0] = Objects.requireNonNull(member).getRoles()
                                                         .stream()
                                                         .anyMatch(role -> role.getId().equalsIgnoreCase(
                                                                 element.getAsString())));
        }
        if (!var[0]) throw new ArkansasException(TemplateMessage.NO_PERMISSION.getMessageEmbed());
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
