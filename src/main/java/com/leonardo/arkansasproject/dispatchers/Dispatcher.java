package com.leonardo.arkansasproject.dispatchers;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.leonardo.arkansasproject.entities.Report;
import com.leonardo.arkansasproject.utils.Commons;
import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.requests.RestAction;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

@Singleton
public class Dispatcher {

    @Inject
    private JDA jda;

    @Inject
    private void loadDispatchers(Dotenv dotenv) {
        Arrays.stream(ReportDispatch.values())
              .forEach(reportDispatch -> reportDispatch.getInfo().load(dotenv));
    }

    public void dispatch(ReportDispatch dispatchTarget, Report report, Bug... bugs) {
        final ReportDispatchInfo destination = dispatchTarget.getInfo();
        report.getAuthor(jda).and(jda.retrieveUserById(report.getLastOperator()), (user, user2) -> {
            final TextChannel channel = Optional.ofNullable(jda.getTextChannelById(destination.getChannelId())).get();
            final EmbedBuilder builder = Commons.buildInfoMsgFrom(report, user, user2, destination.getColorMessage());
            final MessageBuilder messageBuilder = new MessageBuilder(builder);
            messageBuilder.allowMentions(Message.MentionType.USER, Message.MentionType.ROLE);
            if (report.getLastOperator() != null) messageBuilder.mentionUsers(report.getLastOperator());
            if (bugs.length > 0) {
                final Bug bug = bugs[0];
                final String[] roles = bug.getRoles().toArray(new String[]{});
                final Optional<String> optional =
                        Arrays.stream(roles).map(s -> Objects
                                .requireNonNull(jda.getRoleById(s)).getAsMention())
                              .reduce((s, s2) -> s + " " + s2);
                optional.ifPresent(s -> messageBuilder
                        .mentionRoles(roles)
                        .setContent("Etiqueta de " + s + ", " + bug.getTag()));
            }
            return channel
                    .sendMessage(messageBuilder.build())
                    .setActionRow(
                            Button.success("update-report-" + report.getId(), "Atualizar"),
                            Button.secondary("update-report-status-" + report.getId(), "Editar status"));
        }).queue(RestAction::queue);

    }
}
