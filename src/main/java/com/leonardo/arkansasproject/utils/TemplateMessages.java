package com.leonardo.arkansasproject.utils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.*;

public enum TemplateMessages {

    NO_ARGS(new EmbedBuilder()
            .setColor(new Color(59, 56, 209))
            .addField("Erro:", "Você não colocou nenhum título.", false)
            .addField("Sugestão de comando:", "&reportar Digite o seu título", false)
            .build()
    ),
    ARGS_LENGTH_NOT_SUPPORTED(new EmbedBuilder()
            .setColor(new Color(59, 56, 209))
            .addField("Erro:", "A quantidade de caracteres emitidas não é permitida. (6-40)", false)
            .build()
    );


    private final MessageEmbed messageEmbed;

    TemplateMessages(MessageEmbed messageEmbed) {
        this.messageEmbed = messageEmbed;
    }

    public MessageEmbed getMessageEmbed() {
        return messageEmbed;
    }
}
