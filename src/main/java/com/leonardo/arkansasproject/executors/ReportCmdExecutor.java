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
import net.dv8tion.jda.api.interactions.components.Button;
import org.ehcache.Cache;

import java.util.HashSet;
import java.util.concurrent.TimeUnit;

@CommandExecutor(aliases = {"report", "reportar"})
@NoArgsConstructor
public class ReportCmdExecutor implements Executor {

    @Inject
    private Bot bot;

    @Override
    public void exec(MessageReceivedEvent mre, User sender, String[] args) {
        final MessageChannel channel = mre.getChannel();
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
            channel.sendMessage(TemplateMessages.NO_ARGS_REPORT.getMessageEmbed()).complete().delete()
                   .queueAfter(12, TimeUnit.SECONDS);
            return;
        }
        final String title = String.join(" ", args);
        if (!Checker.characterLength(title)) {
            channel.sendMessage(TemplateMessages.TEXT_LENGTH_NOT_SUPPORTED.getMessageEmbed()).complete().delete()
                   .queueAfter(12, TimeUnit.SECONDS);
            return;
        }
        final EmbedBuilder builder = TemplateMessages.TEMPLATE_PROCESSING_REPORT.getEmbedBuilder();
        final MessageBuilder messageBuilder = new MessageBuilder();
        builder.setAuthor(sender.getAsTag() + " (" + sender.getId() + ")");
        builder
                .appendDescription("[")
                .appendDescription(title)
                .appendDescription("](https://github.com/LeonardoCod3r) \n")
                .appendDescription("\n");
        messageBuilder.setEmbed(builder.build());
        final Report report = new Report();
        report.setUserId(sender.getId());
        report.setTitle(title);
        final ReportProcessing reportProcessing = new ReportProcessing(report, this.bot);
        reportProcessing.setMessage(
                channel
                        .sendMessage(messageBuilder.build())
                        .complete()
                        .editMessage(sender.getName() +
                                     ", explique passo a passo a ocorrência do bug. Por fim, clique em \"PRONTO\".")
                        .setActionRow(Button.success("confirm-next", "Pronto"))
                        .complete()
        );
        processing.put(sender.getId(), reportProcessing);
    }
}
