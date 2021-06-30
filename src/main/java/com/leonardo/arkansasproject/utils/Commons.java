package com.leonardo.arkansasproject.utils;

import com.leonardo.arkansasproject.models.Report;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.User;

import java.awt.*;
import java.util.Map;

public class Commons {

    public static EmbedBuilder buildInfoMsgFrom(Report report, User user) {
        final EmbedBuilder builder = new EmbedBuilder()
                .setColor(new Color(59, 56, 209))
                .setAuthor(user.getAsTag() + " (" + user.getId() + ")")
                .setTitle(report.getTitle(), "https://github.com/LeonardoCod3r")
                .appendDescription("\n")
                .addField("Resultado esperado", report.getExpectedOutcome(), false)
                .addField("Resultado real", report.getActualResult(), false)
                .addField("Anomalia ocorrida em", report.getServerName()+ "\n**\n**", false);
        report.getSteps().forEach(s -> builder.appendDescription("- " + s + "\n"));
        builder.appendDescription("_\n_");
        final Map<String, String> attachments = report.getAttachments();
        if (!attachments.isEmpty()) {
            final StringBuilder stringBuilder = new StringBuilder();
            attachments.forEach((s, s2) -> stringBuilder.append(s).append(": ").append(s2).append("\n"));
            builder.addField("Anexos", stringBuilder.toString(), false);
        }
        builder.setTimestamp(report.getDate().toInstant());
        builder.setFooter("ID: #" + report.getId());
        return builder;
    }
}
