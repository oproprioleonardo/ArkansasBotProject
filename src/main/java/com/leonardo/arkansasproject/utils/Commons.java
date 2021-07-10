package com.leonardo.arkansasproject.utils;

import com.leonardo.arkansasproject.models.Report;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;

import java.awt.*;
import java.util.Map;

public class Commons {

    public static EmbedBuilder buildInfoMsgFrom(Report report, User user) {
        return buildInfoMsgFrom(report, user, new Color(59, 56, 209));
    }

    public static EmbedBuilder buildInfoMsgFrom(Report report, User user, Color color) {
        final EmbedBuilder builder = new EmbedBuilder()
                .setColor(color)
                .setAuthor(user.getAsTag() + " (" + user.getId() + ")")
                .appendDescription("**[" + report.getTitle() + "]" + "(https://hylex.me/bugs)**\n")
                .appendDescription("\n")
                .addField("Resultado esperado", report.getExpectedOutcome(), false)
                .addField("Resultado real", report.getActualResult(), false)
                .addField("Servidor afetado", report.getServerName() + "\n", false);
        report.getSteps().forEach(s -> builder.appendDescription("- " + s + "\n"));
        builder.appendDescription("\n");
        final Map<String, String> attachments = report.getAttachments();
        if (!attachments.isEmpty()) {
            final StringBuilder stringBuilder = new StringBuilder();
            attachments.forEach(
                    (s, s2) -> stringBuilder.append("**[").append(s).append("](").append(s2).append(")**")
                                            .append("\n"));
            builder.addField("Anexos", stringBuilder.toString(), false);
        }
        builder.setTimestamp(report.getDate().toInstant());
        builder.setFooter("#" + report.getId() + " • " + report.getStatus().getLabel());
        return builder;
    }
}
