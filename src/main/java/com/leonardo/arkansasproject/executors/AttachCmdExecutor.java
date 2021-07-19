package com.leonardo.arkansasproject.executors;

import com.google.inject.Inject;
import com.leonardo.arkansasproject.services.ReportService;
import com.leonardo.arkansasproject.utils.TemplateMessage;
import com.leonardo.arkansasproject.validators.TextValidator;
import com.leonardo.arkansasproject.validators.exceptions.ArkansasException;
import lombok.NoArgsConstructor;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Arrays;

@CommandExecutor(aliases = {"attach", "anexar"})
@NoArgsConstructor
public class AttachCmdExecutor implements Executor {

    @Inject
    private ReportService reportService;

    @Override
    public void exec(MessageReceivedEvent mre, User sender, String[] args) {
        final MessageChannel channel = mre.getChannel();
        String text;
        try {
            TextValidator.hasArgsOrThrow(args, 3, TemplateMessage.NO_ARGS_ATTACH);
            TextValidator.isUrlOrThrow(args[1]);
            final String[] textArgs = Arrays.copyOfRange(args, 2, (args.length));
            text = String.join(" ", textArgs);
            TextValidator.hasCharLenghtOrThrow(text, 1, 40);
        } catch (ArkansasException e) {
            e.throwMessage(channel);
            return;
        }
        final long id = Long.parseLong(args[0]);
        this.reportService.read(id)
                          .onItem().ifNull().fail().onFailure()
                          .invoke(() -> channel.sendMessage(TemplateMessage.NOT_EXISTS_REPORT.getMessageEmbed())
                                               .queue())
                          .onItem().ifNotNull().call(report -> {
            report.attach(text, args[1]);
            return this.reportService.update(report).onItem().ifNotNull()
                                     .invoke(() -> channel
                                             .sendMessage(TemplateMessage.SAVE_SUCCESS.getMessageEmbed())
                                             .queue()).onItem().ifNull().fail().onFailure()
                                     .invoke(() -> channel
                                             .sendMessage(TemplateMessage.REPORT_SAVE_ERROR.getMessageEmbed())
                                             .queue());
        }).await().indefinitely();
    }
}
