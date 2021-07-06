package com.leonardo.arkansasproject.executors;

import com.google.inject.Inject;
import com.leonardo.arkansasproject.managers.ReportProcessingManager;
import com.leonardo.arkansasproject.models.Report;
import com.leonardo.arkansasproject.models.ReportProcessing;
import com.leonardo.arkansasproject.validators.TextValidator;
import com.leonardo.arkansasproject.utils.TemplateMessages;
import lombok.NoArgsConstructor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.components.Button;

import java.util.concurrent.TimeUnit;

@CommandExecutor(aliases = {"report", "reportar"})
@NoArgsConstructor
public class ReportCmdExecutor implements Executor {

    @Inject
    private ReportProcessingManager manager;

    @Override
    public void exec(MessageReceivedEvent mre, User sender, String[] args) {
        final MessageChannel channel = mre.getChannel();
        if (manager.exists(sender.getIdLong())) {
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
        if (!TextValidator.characterLength(title)) {
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
        final ReportProcessing reportProcessing = new ReportProcessing(report);
        reportProcessing.message =
                channel
                        .sendMessage(messageBuilder.build())
                        .complete()
                        .editMessage(sender.getName() +
                                     ", explique passo a passo a ocorrência do bug. Por fim, clique em \"PRONTO\".")
                        .setActionRow(Button.success("confirm-next", "Pronto"))
                        .complete();
        manager.put(sender.getIdLong(), reportProcessing);
    }
}
