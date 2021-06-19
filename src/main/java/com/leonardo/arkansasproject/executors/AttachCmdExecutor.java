package com.leonardo.arkansasproject.executors;

import com.google.inject.Inject;
import com.leonardo.arkansasproject.Bot;
import lombok.NoArgsConstructor;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

@CommandExecutor(aliases = {"attach", "anexar"})
@NoArgsConstructor
public class AttachCmdExecutor implements Executor{

    @Inject
    private Bot bot;

    @Override
    public void exec(MessageReceivedEvent mre, User sender, String[] args) {

    }
}
