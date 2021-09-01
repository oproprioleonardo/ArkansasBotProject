package com.leonardo.arkansasproject.executors;

import com.leonardo.arkansasproject.utils.TemplateMessage;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

@CommandExecutor(aliases = {"help", "ajuda"})
public class HelpCmdExecutor implements Executor {

    @Override
    public void exec(MessageReceivedEvent mre, User sender, String[] args) {
        mre.getChannel().sendMessageEmbeds(TemplateMessage.HELP.getMessageEmbed()).queue();
    }
}
