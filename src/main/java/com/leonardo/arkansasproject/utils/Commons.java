package com.leonardo.arkansasproject.utils;

import com.leonardo.arkansasproject.entities.Report;
import com.leonardo.arkansasproject.report.ReportStatus;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;

import java.awt.*;
import java.util.Locale;
import java.util.Map;

public class Commons {

    public static EmbedBuilder buildInfoMsgFrom(Report report, User user, User operator) {
        return buildInfoMsgFrom(report, user, operator, new Color(102, 180, 241));
    }

    public static EmbedBuilder buildInfoMsgFrom(Report report, User user, User operator, Color color) {
        final EmbedBuilder builder = new EmbedBuilder()
                .setColor(color)
                .setAuthor(user.getAsTag() + " (" + user.getId() + ")")
                .appendDescription("**[" + report.getTitle() + "]" + "(https://equipe.hylex.me/bugs-and-falhas)**\n")
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
        final ReportStatus status = report.getStatus();
        if (status != ReportStatus.ACTIVATED) {
            final String content = "O bug foi " + status.getLabel().toLowerCase(Locale.ROOT) +
                                   " por " + operator.getAsMention();
            final String emoji =
                    report.getStatus() == ReportStatus.ARCHIVED ?
                    "<:arquivado:882078447491497994>" :
                    report.getStatus() == ReportStatus.ACCEPTED ?
                    "<:aprovado:882078447642476585>" : "<:negado:882078447613149184>";
            builder.addField(emoji + " " + status.getLabel(), content, false);
        }
        builder.setFooter("#" + report.getId());
        return builder;
    }
}
