package com.leonardo.arkansasproject.executors;

import com.google.inject.Inject;
import com.leonardo.arkansasproject.Bot;
import com.leonardo.arkansasproject.models.Report;
import com.leonardo.arkansasproject.models.suppliers.ReportProcessing;
import com.leonardo.arkansasproject.utils.Checker;
import com.leonardo.arkansasproject.utils.TemplateMessages;
import lombok.NoArgsConstructor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.ehcache.Cache;

import java.awt.*;
import java.util.HashSet;

@CommandExecutor(aliases = {"report", "reportar"})
@NoArgsConstructor
public class ReportCmdExecutor implements Executor {

    @Inject
    private Bot bot;

    @Override
    public void exec(MessageReceivedEvent mre, User sender, String[] args) {
        final MessageChannel channel = mre.getChannel();
        if (mre.isWebhookMessage() || mre.isFromGuild()) return;
        final Cache<String, ReportProcessing> processing = this.bot.REPORT_PROCESSING;
        final boolean match = processing.getAll(new HashSet<>()).keySet().stream()
                                        .anyMatch(userId -> userId.equalsIgnoreCase(sender.getId()));
        if (match) {
            channel.sendMessage(sender.getAsMention() +
                                ", você já está fazendo um relatório. Aguarde alguns segundos ou complete o existente.")
                   .queue();
            return;
        }
        if (args.length == 0) {
            channel.sendMessage(TemplateMessages.NO_ARGS.getMessageEmbed()).queue();
            return;
        }
        final String title = String.join(" ", args);
        if (!Checker.characterLength(title)) {
            channel.sendMessage(TemplateMessages.ARGS_LENGTH_NOT_SUPPORTED.getMessageEmbed()).queue();
            return;
        }
        final Report report = new Report();
        report.setUserId(sender.getId());
        report.setTitle(title);
        final EmbedBuilder builder = new EmbedBuilder();
        final MessageBuilder messageBuilder = new MessageBuilder();
        builder.setColor(new Color(59, 56, 209));
        builder.setAuthor(sender.getAsTag() + " (" + sender.getId() + ")");
        final StringBuilder descBuilder = builder.getDescriptionBuilder();
        descBuilder
                .append("\n\n")
                .append("[")
                .append(title)
                .append("](https://github.com/LeonardoCod3r) \n")
                .append("\n");
        messageBuilder.setContent(
                sender.getAsMention() + ", explique passo a passo a ocorrência do bug. Por fim, digite \"PRONTO\".");
        messageBuilder.setEmbed(builder.build());
        final ReportProcessing reportProcessing = new ReportProcessing(report, this.bot);
        reportProcessing.setMessage(channel.sendMessage(messageBuilder.build()).complete());
        processing.put(sender.getId(), reportProcessing);
    }
}
