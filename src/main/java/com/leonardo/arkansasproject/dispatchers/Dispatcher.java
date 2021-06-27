package com.leonardo.arkansasproject.dispatchers;

import com.google.inject.Inject;
import com.leonardo.arkansasproject.models.Report;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.Optional;

public class Dispatcher {

    @Inject
    private JDA jda;

    public void dispatch(ReportDispatchDestination dispatchTarget, Report report) {
        final User user = report.getAuthor();
        final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy - HH:mm:ss");
        final Optional<TextChannel> channel =
                Optional.ofNullable(jda.getTextChannelById(dispatchTarget.getChannelId()));
        final EmbedBuilder builder = new EmbedBuilder()
                .setColor(new Color(59, 56, 209))
                .setAuthor(user.getAsTag() + " (" + user.getId() + ")")
                .appendDescription("\n\n")
                .appendDescription("[")
                .appendDescription(report.getTitle())
                .appendDescription("](https://github.com/LeonardoCod3r) - #" + report.getId() + "\n")
                .appendDescription("\n")
                .addField("Resultado esperado", report.getExpectedOutcome(), false)
                .addField("Resultado real", report.getActualResult(), false)
                .addField("Anomalia ocorrida em", report.getServerName(), false)
                .addField("Data de criação", dateFormat.format(report.getDate().getTime()), false);
        report.getSteps().forEach(s -> builder.appendDescription("- " + s + "\n"));
        final Map<String, String> attachments = report.getAttachments();
        if (!attachments.isEmpty()) {
            final StringBuilder stringBuilder = new StringBuilder();
            attachments.forEach((s, s2) -> stringBuilder.append(s).append(": ").append(s2).append("\n"));
            builder.addField("Anexos", stringBuilder.toString(), false);
        }
        channel.ifPresent(textChannel -> textChannel.sendMessage(builder.build()).queue());
    }
}
