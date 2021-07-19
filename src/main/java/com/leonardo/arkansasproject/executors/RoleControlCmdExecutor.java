package com.leonardo.arkansasproject.executors;

import com.google.inject.Inject;
import com.leonardo.arkansasproject.managers.ConfigManager;
import com.leonardo.arkansasproject.utils.TemplateMessage;
import com.leonardo.arkansasproject.validators.TextValidator;
import com.leonardo.arkansasproject.validators.exceptions.ArkansasException;
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
            TextValidator.hasArgsOrThrow(args, 3, TemplateMessage.NO_ARGS_TAGCONTROL);
        } catch (ArkansasException e) {
            e.throwMessage(channel);
            return;
        }
        final String bugId = args[1];
        final String roleId = args[2];
        if (args[0].equalsIgnoreCase("add")) {
            this.configManager.addRoleAtBug(bugId, roleId);
        } else if (args[0].equalsIgnoreCase("rem")) {
            this.configManager.removeRoleAtBug(bugId, roleId);
        } else return;
        channel.sendMessage(TemplateMessage.SAVE_SUCCESS.getMessageEmbed()).queue();
        try {
            this.configManager.editConfig();
        } catch (IOException e) {
            channel.sendMessage(TemplateMessage.INTERNAL_ERROR.getMessageEmbed()).complete().delete()
                   .queueAfter(15, TimeUnit.SECONDS);
            e.printStackTrace();
        }
    }
}
