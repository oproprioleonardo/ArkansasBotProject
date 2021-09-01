package com.leonardo.arkansasproject.report;

import com.leonardo.arkansasproject.entities.Report;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;

import java.awt.*;
import java.util.function.BiConsumer;

@NoArgsConstructor(access = AccessLevel.NONE)
public class ReportProcessing {

    @Getter
    public final Report report;
    public Message message;
    @Getter
    private ReportProcessingStatus processingState = ReportProcessingStatus.ATTACH_STEP_BY_STEP;

    public ReportProcessing(Report report) {
        this.report = report;
    }

    public MessageEmbed buildMessage(User user) {
        final EmbedBuilder embedBuilder = new EmbedBuilder()
                .setColor(new Color(102, 180, 241))
                .appendDescription("**[" + report.getTitle() + "]" + "(https://equipe.hylex.me/bugs-and-falhas)**\n")
                .setAuthor(user.getAsTag() + " (" + user.getId() + ")")
                .appendDescription("\n");
        report.getSteps().forEach(s -> embedBuilder
                .appendDescription("- ")
                .appendDescription(s)
                .appendDescription("\n"));
        embedBuilder.appendDescription("\n");
        if (report.getExpectedOutcome() != null)
            embedBuilder.addField("Resultado esperado", report.getExpectedOutcome(), false);
        if (report.getActualResult() != null)
            embedBuilder.addField("Resultado real", report.getActualResult(), false);
        if (report.getServerName() != null)
            embedBuilder.addField("Servidor afetado", report.getServerName() + "\n", false);
        if (report.getId() != null) {
            embedBuilder.setTimestamp(report.getDate().toInstant());
            embedBuilder.setFooter("#" + report.getId() + " • " + report.getStatus().getLabel());
        }
        return embedBuilder.build();
    }

    public void updateMessage(User user) {
        this.message = this.message.editMessage(buildMessage(user)).complete();
    }

    public void next(User user, BiConsumer<ReportProcessing, Boolean> consumer) {
        this.updateMessage(user);
        if (!this.processingState.hasNext()) {
            consumer.accept(this, false);
            return;
        }
        this.processingState = processingState.nextState();
        consumer.accept(this, true);
    }

}
