package com.leonardo.arkansasproject.executors;

import com.google.inject.Inject;
import com.leonardo.arkansasproject.managers.ConfigManager;
import com.leonardo.arkansasproject.utils.TemplateMessages;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@CommandExecutor(aliases = {"etiqueta", "tagcontrol"})
public class RoleControlCmdExecutor implements Executor {

    @Inject
    private ConfigManager configManager;

    @Override
    public void exec(MessageReceivedEvent mre, User sender, String[] args) {
        final MessageChannel channel = mre.getChannel();
        try {
            final String bugId = args[1];
            final String roleId = args[2];
            if (args[0].equalsIgnoreCase("add")) {
                this.configManager.addRoleAtBug(bugId, roleId);
            } else if (args[0].equalsIgnoreCase("rem")) {
                this.configManager.removeRoleAtBug(bugId, roleId);
            }
            channel.sendMessage(TemplateMessages.SAVE_SUCCESS.getMessageEmbed()).queue();
        } catch (Exception e) {
            channel.sendMessage(TemplateMessages.NO_ARGS_TAGCONTROL.getMessageEmbed()).complete().delete()
                   .queueAfter(12, TimeUnit.SECONDS);
            return;
        }
        try {
            this.configManager.editConfig();
        } catch (IOException e) {
            channel.sendMessage(TemplateMessages.INTERNAL_ERROR.getMessageEmbed()).complete().delete()
                   .queueAfter(15, TimeUnit.SECONDS);
            e.printStackTrace();
        }
    }
}
