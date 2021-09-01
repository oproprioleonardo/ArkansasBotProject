package com.leonardo.arkansasproject.executors;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.leonardo.arkansasproject.entities.Report;
import com.leonardo.arkansasproject.managers.ReportProcessingManager;
import com.leonardo.arkansasproject.report.ReportProcessing;
import com.leonardo.arkansasproject.utils.TemplateMessage;
import com.leonardo.arkansasproject.validators.Validators;
import com.leonardo.arkansasproject.validators.exceptions.ArkansasException;
import lombok.NoArgsConstructor;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.components.Button;

@CommandExecutor(aliases = {"report", "reportar", "bug"})
@NoArgsConstructor
@Singleton
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

        final String title = String.join(" ", args);
        try {
            Validators.hasArgsOrThrow(args, 1, TemplateMessage.NO_ARGS_REPORT);
            Validators.hasCharLenghtOrThrow(title);
        } catch (ArkansasException e) {
            e.throwMessage(channel);
            return;
        }

        final MessageBuilder messageBuilder = new MessageBuilder();
        final Report report = new Report();
        report.setUserId(sender.getId());
        report.setLastOperator(sender.getId());
        report.setTitle(title);
        final ReportProcessing reportProcessing = new ReportProcessing(report);
        final MessageEmbed embed = reportProcessing.buildMessage(sender);
        messageBuilder.setEmbeds(embed);
        reportProcessing.message =
                channel
                        .sendMessage(messageBuilder.build())
                        .complete()
                        .editMessage("**" + sender.getName() +
                                     "**, obrigado por querer reportar uma falha da rede. O título de apresentação foi definido com sucesso!\n" +
                                     "Agora é necessário que você explique passo a passo como reproduzir o bug. Quando terminar de explicar clique no botão.")
                        .setActionRow(Button.success("confirm-next", "Pronto"))
                        .complete();
        manager.put(sender.getIdLong(), reportProcessing);
    }
}
