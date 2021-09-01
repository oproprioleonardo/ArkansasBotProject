package com.leonardo.arkansasproject.executors;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.leonardo.arkansasproject.managers.ReportProcessingManager;
import com.leonardo.arkansasproject.report.ReportProcessing;
import com.leonardo.arkansasproject.utils.TemplateMessage;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

@CommandExecutor(aliases = {"cancel", "cancelar"})
@Singleton
public class CancelCmdExecutor implements Executor {

    @Inject
    private ReportProcessingManager manager;

    @Override
    public void exec(MessageReceivedEvent mre, User sender, String[] args) {
        final MessageChannel channel = mre.getChannel();
        if (manager.exists(sender.getIdLong())) {
            final ReportProcessing processing = manager.get(sender.getIdLong());
            processing.message.delete().queue();
            channel.sendMessageEmbeds(TemplateMessage.CANCELED.getMessageEmbed()).queue();
            manager.remove(sender.getIdLong());
        }
    }
}
