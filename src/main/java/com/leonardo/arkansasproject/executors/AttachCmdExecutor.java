package com.leonardo.arkansasproject.executors;

import com.google.inject.Inject;
import com.leonardo.arkansasproject.services.ReportService;
import com.leonardo.arkansasproject.validators.TextValidator;
import com.leonardo.arkansasproject.utils.TemplateMessages;
import io.smallrye.mutiny.Uni;
import lombok.NoArgsConstructor;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@CommandExecutor(aliases = {"attach", "anexar"})
@NoArgsConstructor
public class AttachCmdExecutor implements Executor {

    @Inject
    private ReportService reportService;

    @Override
    public void exec(MessageReceivedEvent mre, User sender, String[] args) {
        final MessageChannel channel = mre.getChannel();
        if (args.length < 3) {
            channel.sendMessage(TemplateMessages.NO_ARGS_ATTACH.getMessageEmbed()).complete().delete()
                   .queueAfter(12, TimeUnit.SECONDS);
            return;
        }
        final long id = Long.parseLong(args[0]);
        this.reportService.read(id)
                          .onItem().ifNull().fail().onFailure()
                          .invoke(() -> channel.sendMessage(TemplateMessages.NOT_EXISTS_REPORT.getMessageEmbed())
                                               .queue())
                          .onItem().ifNotNull().call(report -> {
            if (!TextValidator.isUrl(args[1])) {
                channel.sendMessage(TemplateMessages.NOT_URL.getMessageEmbed()).complete().delete()
                       .queueAfter(12, TimeUnit.SECONDS);
                return Uni.createFrom().nothing();
            }
            final String[] textArgs = Arrays.copyOfRange(args, 2, (args.length));
            final String text = String.join(" ", textArgs);
            if (!TextValidator.characterLength(text, 1, 40)) {
                channel.sendMessage(TemplateMessages.TEXT_LENGTH_NOT_SUPPORTED.getMessageEmbed()).complete()
                       .delete()
                       .queueAfter(12, TimeUnit.SECONDS);
                return Uni.createFrom().nothing();
            }
            report.attach(text, args[1]);
            return this.reportService.update(report).onItem().ifNotNull()
                                     .invoke(() -> channel
                                             .sendMessage(TemplateMessages.REPORT_SAVE_SUCCESS.getMessageEmbed())
                                             .queue()).onItem().ifNull().fail().onFailure()
                                     .invoke(() -> channel
                                             .sendMessage(TemplateMessages.REPORT_SAVE_ERROR.getMessageEmbed())
                                             .queue());
        }).await().indefinitely();

    }
}
