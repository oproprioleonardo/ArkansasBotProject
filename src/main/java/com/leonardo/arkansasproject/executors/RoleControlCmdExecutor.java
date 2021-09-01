package com.leonardo.arkansasproject.executors;

import com.google.inject.Inject;
import com.leonardo.arkansasproject.utils.BotConfig;
import com.leonardo.arkansasproject.utils.TemplateMessage;
import com.leonardo.arkansasproject.validators.Validators;
import com.leonardo.arkansasproject.validators.exceptions.ArkansasException;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@CommandExecutor(aliases = {"etiqueta", "tagcontrol"})
public class RoleControlCmdExecutor implements Executor {

    @Inject
    private BotConfig botConfig;

    @Override
    public void exec(MessageReceivedEvent mre, User sender, String[] args) {
        final MessageChannel channel = mre.getChannel();
        try {
            Validators.isAdmin(mre, botConfig);
            Validators.hasArgsOrThrow(args, 3, TemplateMessage.NO_ARGS_TAGCONTROL);
        } catch (ArkansasException e) {
            e.throwMessage(channel);
            return;
        }
        final String bugId = args[1];
        final String roleId = args[2];
        if (args[0].equalsIgnoreCase("add")) {
            this.botConfig.addRoleAtBug(bugId, roleId);
        } else if (args[0].equalsIgnoreCase("rem")) {
            this.botConfig.removeRoleAtBug(bugId, roleId);
        } else return;
        channel.sendMessageEmbeds(TemplateMessage.SAVE_SUCCESS.getMessageEmbed()).queue();
        try {
            this.botConfig.editConfig();
        } catch (IOException e) {
            channel.sendMessageEmbeds(TemplateMessage.INTERNAL_ERROR.getMessageEmbed()).complete().delete()
                   .queueAfter(15, TimeUnit.SECONDS);
            e.printStackTrace();
        }
    }
}
