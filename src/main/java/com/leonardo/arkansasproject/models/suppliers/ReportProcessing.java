package com.leonardo.arkansasproject.models.suppliers;

import com.google.common.collect.Lists;
import com.leonardo.arkansasproject.Bot;
import com.leonardo.arkansasproject.models.Report;
import lombok.*;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

import java.awt.*;
import java.util.List;
import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.NONE)
public class ReportProcessing {

    @Getter
    private final Report report;
    private final Bot bot;
    private final List<String> alreadyPut = Lists.newArrayList();
    @Getter
    @Setter
    private ReportProcessingState processingState = ReportProcessingState.ATTACH_STEP_BY_STEP;
    @Setter
    private Message message;
    private final EmbedBuilder builder;
    private final StringBuilder descBuilder;

    public ReportProcessing(Report report, Bot bot) {
        this.report = report;
        this.bot = bot;
        this.builder = new EmbedBuilder()
                .setColor(new Color(59, 56, 209))
                .setAuthor(report.getAuthor().getAsTag() + " (" + report.getUserId() + ")");
        this.descBuilder = this.builder.getDescriptionBuilder()
                                       .append("\n\n").append("[")
                                       .append(report.getTitle())
                                       .append("](https://github.com/LeonardoCod3r)")
                                       .append("\n")
                                       .append("\n");
    }

    public void updateMessage() {
        this.message = this.message.editMessage(builder.build()).complete();
    }

    public void updateStepsDesc() {
        report.getSteps().forEach(s -> {
            if (alreadyPut.stream().noneMatch(s1 -> s1.equalsIgnoreCase(s))) {
                descBuilder.append("- ").append(s).append("\n");
                alreadyPut.add(s);
            }
        });
    }

    public void updateExpectedResultField() {
        if (report.getExpectedOutcome() != null) {
            if (builder.getFields().stream().noneMatch(field -> Objects.requireNonNull(field.getName())
                                                                       .equalsIgnoreCase("Resultado esperado"))) {
                builder.addField("Resultado esperado", report.getExpectedOutcome(), false);
            }
        }
    }

    public void updateActualResultField() {
        if (report.getActualResult() != null) {
            if (builder.getFields().stream().noneMatch(field -> Objects.requireNonNull(field.getName())
                                                                       .equalsIgnoreCase("Resultado real"))) {
                builder.addField("Resultado real", report.getActualResult(), false);
            }
        }
    }

    public void updateServerField() {
        if (report.getServerName() != null) {
            if (builder.getFields().stream().noneMatch(field -> Objects.requireNonNull(field.getName())
                                                                       .equalsIgnoreCase("Anomalia ocorrida em"))) {
                builder.addField("Anomalia ocorrida em", report.getServerName(), false);
            }
        }
    }

    public void updateAllFields() {
        updateStepsDesc();
        updateExpectedResultField();
        updateActualResultField();
        updateServerField();
    }

    public void next() {
        this.updateAllFields();
        this.updateMessage();
        if (!this.processingState.hasNext()) {
            this.bot.REPORT_PROCESSING.remove(report.getUserId());
            final MessageChannel channel = this.message.getChannel();
            this.message.delete().queue();
            channel.sendMessage(new MessageBuilder()
                                        .setEmbed(builder.build())
                                        .setContent("Parabéns! Você registrou o bug com sucesso.").build())
                   .queue();
            return;
        }
        this.processingState = processingState.nextState();
        switch (processingState) {
            case ATTACH_ACTUAL_RESULT:
                this.message = message.editMessage(
                        report.getAuthor().getAsMention() + ", diga o que realmente aconteceu em poucas palavras.")
                                      .complete();
                break;
            case ATTACH_EXPECTED_RESULT:
                this.message = message.editMessage(
                        report.getAuthor().getAsMention() + ", diga o que era correto acontecer em poucas palavras.")
                                      .complete();
                break;
            case ATTACH_SERVER:
                this.message = message.editMessage(
                        report.getAuthor().getAsMention() + ", diga em qual servidor o bug ocorreu.").complete();
                break;
            default:
                break;
        }
    }


}
