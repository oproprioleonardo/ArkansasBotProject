package com.leonardo.arkansasproject.executors;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public interface Executor {

    void exec(MessageReceivedEvent mre, User sender, String[] args);

}
