package com.leonardo.arkansasproject.utils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.*;

public enum TemplateMessage {

    NO_ARGS_REPORT(new EmbedBuilder()
                           .setColor(new Color(59, 56, 209))
                           .addField("Erro:", "Você não colocou nenhum título.", false)
                           .addField("Sugestão de comando:", "&reportar Digite o seu título", false)
    ),
    NO_ARGS_ATTACH(new EmbedBuilder()
                           .setColor(new Color(59, 56, 209))
                           .addField("Erro:", "Quantidade de argumentos não suportada", false)
                           .addField("Sugestão de comando:", "&anexar [reportId] [url] [descrição]", false)
    ),
    NO_ARGS_TAGCONTROL(new EmbedBuilder()
                               .setColor(new Color(59, 56, 209))
                               .addField("Erro:", "Quantidade de argumentos não suportada", false)
                               .addField("Sugestão de comando:", "&etiqueta (add/rem) [bugId] [roleId]", false)
    ),
    INTERNAL_ERROR(new EmbedBuilder()
                           .setColor(new Color(215, 1, 1))
                           .addField("Erro:", "Ocorreu algum erro interno, por favor verifique o console.",
                                     false)
    ),
    TEXT_LENGTH_NOT_SUPPORTED(new EmbedBuilder()
                                      .setColor(new Color(59, 56, 209))
                                      .addField("Erro:", "A quantidade de caracteres emitidas não é permitida.",
                                                false)
    ),
    NOT_URL(new EmbedBuilder()
                    .setColor(new Color(59, 56, 209))
                    .addField("Erro:", "O texto especificado não corresponde a uma url.", false)
    ),
    NOT_EXISTS_REPORT(new EmbedBuilder()
                              .setColor(new Color(59, 56, 209))
                              .addField("Erro:", "O id especificado não corresponde a nenhum relatório.", false)
    ),
    NO_STEPS(new EmbedBuilder()
                     .setColor(new Color(59, 56, 209))
                     .addField("Erro:", "Você não explicou como é feito o bug.", false)
                     .addField("Sugestão:", "\"Eu digitei blabla e apareceu um erro na tela.\"", false)
    ),
    REPORT_SAVE_ERROR(new EmbedBuilder()
                              .setColor(new Color(59, 56, 209))
                              .addField("Erro:", "Não foi possível salvar as alterações.", false)
    ),
    SAVE_SUCCESS(new EmbedBuilder()
                         .setColor(new Color(59, 56, 209))
                         .appendDescription("As alterações foram salvas com sucesso.")
    ),
    REPORT_SUCCESS(new EmbedBuilder()
                           .setColor(new Color(59, 56, 209))
                           .appendDescription("Olá, sou o responsável pela filtragem de bugs!")
                           .appendDescription("\n\n")
                           .appendDescription("**Obrigado por reportar!** O seu bug foi enviado")
                           .appendDescription("\n")
                           .appendDescription("para a nossa central de bugs e erros e em breve")
                           .appendDescription("\n")
                           .appendDescription("iremos analisá-lo de forma a implementarmos")
                           .appendDescription("\n")
                           .appendDescription("uma correção definitiva.")
                           .appendDescription("\n\n")
                           .appendDescription("Anexar ficheiros: ```&anexar [ID] [link] [desc].```")
                           .appendDescription("\n")
                           .appendDescription("Cuidado para não colocar uma descrição maior")
                           .appendDescription("\n")
                           .appendDescription("que 40 caracteres.")
                           .appendDescription("\n\n")
                           .appendDescription("Abaixo, encontra-se a forma de apresentação como")
                           .appendDescription("\n")
                           .appendDescription("o seu bug irá aparecer para toda a equipe.")
                           .setThumbnail("https://cdn.discordapp.com/emojis/822423373685850143.png?v=1")
    ),
    TEMPLATE_PROCESSING_REPORT(new EmbedBuilder()
                                       .setColor(new Color(59, 56, 209))
                                       .appendDescription("\n\n")
    );


    private final EmbedBuilder embedBuilder;

    TemplateMessage(EmbedBuilder embedBuilder) {
        this.embedBuilder = embedBuilder;
    }

    public EmbedBuilder getEmbedBuilder() {
        return new EmbedBuilder(embedBuilder);
    }

    public MessageEmbed getMessageEmbed() {
        return this.embedBuilder.build();
    }
}
