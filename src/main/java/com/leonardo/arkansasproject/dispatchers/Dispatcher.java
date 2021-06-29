package com.leonardo.arkansasproject.dispatchers;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.leonardo.arkansasproject.models.Report;
import com.leonardo.arkansasproject.utils.Commons;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.components.Button;

import java.util.Optional;

@Singleton
public class Dispatcher {

    @Inject
    private JDA jda;

    public void dispatch(ReportDispatch dispatchTarget, Report report) {
        final User user = report.getAuthor(jda);
        final Optional<TextChannel> channel =
                Optional.ofNullable(jda.getTextChannelById(dispatchTarget.getInstance().getChannelId()));
        final EmbedBuilder builder = Commons.buildInfoMsgFrom(report, user);
        channel.ifPresent(textChannel -> textChannel
                .sendMessage(builder.build())
                .setActionRow(
                        Button.success("update-report-" + report.getId(), "Atualizar"),
                        Button.secondary("update-report-status-" + + report.getId(), "Editar status")
                ).queue());
    }
}
