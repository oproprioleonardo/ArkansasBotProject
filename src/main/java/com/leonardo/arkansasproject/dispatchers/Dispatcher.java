package com.leonardo.arkansasproject.dispatchers;

import com.google.gson.JsonObject;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.leonardo.arkansasproject.models.Report;
import com.leonardo.arkansasproject.utils.Commons;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.requests.restaction.MessageAction;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Optional;

@Singleton
public class Dispatcher {

    @Inject
    private JDA jda;
    @Inject
    @Named(value = "main_config")
    private JsonObject config;

    public void dispatch(ReportDispatch dispatchTarget, Report report, String... roleMentions) {
        final User user = report.getAuthor(jda);
        final Optional<TextChannel> channel =
                Optional.ofNullable(jda.getTextChannelById(dispatchTarget.getInstance(config).getChannelId()));
        final EmbedBuilder builder = Commons.buildInfoMsgFrom(report, user);
        channel.ifPresent(textChannel -> {
            MessageAction action = textChannel
                    .sendMessage(builder.build())
                    .setActionRow(
                            Button.success("update-report-" + report.getId(), "Atualizar"),
                            Button.secondary("update-report-status-" + +report.getId(), "Editar status"));
            final Optional<String> optional =
                    Arrays.stream(roleMentions).map(s -> Objects.requireNonNull(jda.getRoleById(s)).getAsMention())
                          .reduce((s, s2) -> s + ", " + s2);
            if (optional.isPresent() && !optional.get().isEmpty()) action = action.allowedMentions(
                    EnumSet.of(Message.MentionType.ROLE)).mentionRoles(roleMentions).content(optional.get());
            action.queue();
        });
    }
}
