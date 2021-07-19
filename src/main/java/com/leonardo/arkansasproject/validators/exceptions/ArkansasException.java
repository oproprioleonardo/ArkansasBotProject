package com.leonardo.arkansasproject.validators.exceptions;

import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.util.concurrent.TimeUnit;

@Getter
@Setter
public class ArkansasException extends Exception {

    private MessageEmbed messageEmbed;

    public ArkansasException(MessageEmbed messageEmbed) {
        this.messageEmbed = messageEmbed;
    }

    public ArkansasException() {
    }

    public void throwMessage(MessageChannel channel) {
        channel.sendMessage(this.messageEmbed).complete().delete()
               .queueAfter(12, TimeUnit.SECONDS);
    }
}
