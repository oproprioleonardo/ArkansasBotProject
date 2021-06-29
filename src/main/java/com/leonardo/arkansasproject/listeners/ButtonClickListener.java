package com.leonardo.arkansasproject.listeners;

import com.google.inject.Inject;
import com.leonardo.arkansasproject.dispatchers.Dispatcher;
import com.leonardo.arkansasproject.dispatchers.ReportDispatch;
import com.leonardo.arkansasproject.managers.ReportProcessingManager;
import com.leonardo.arkansasproject.models.Report;
import com.leonardo.arkansasproject.models.ReportStatus;
import com.leonardo.arkansasproject.models.suppliers.ReportProcessing;
import com.leonardo.arkansasproject.services.ReportService;
import com.leonardo.arkansasproject.utils.Commons;
import com.leonardo.arkansasproject.utils.TemplateMessages;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.Button;

import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public class ButtonClickListener extends ListenerAdapter {

    @Inject
    private ReportProcessingManager manager;
    @Inject
    private ReportService service;
    @Inject
    private Dispatcher dispatcher;

    @Override
    public void onButtonClick(ButtonClickEvent event) {
        final User user = event.getUser();
        final MessageChannel channel = event.getMessageChannel();
        final Message message = event.getMessage();
        final String componentId = event.getComponentId();
        if (message != null) {
            if ("confirm-next".equals(componentId)) {
                if (!manager.exists(user.getIdLong())) return;
                final ReportProcessing reportProcessing = manager.get(user.getIdLong());
                final Report report = reportProcessing.getReport();
                if (report.getSteps().isEmpty()) {
                    channel.sendMessage(TemplateMessages.NO_STEPS.getMessageEmbed()).complete().delete()
                           .queueAfter(12, TimeUnit.SECONDS);
                } else {
                    message.delete().queue();
                    final EmbedBuilder builder = TemplateMessages.TEMPLATE_PROCESSING_REPORT.getEmbedBuilder();
                    final MessageBuilder messageBuilder = new MessageBuilder();
                    builder.setAuthor(user.getAsTag() + " (" + user.getId() + ")");
                    builder
                            .appendDescription("[")
                            .appendDescription(report.getTitle())
                            .appendDescription("](https://github.com/LeonardoCod3r) \n")
                            .appendDescription("\n");
                    messageBuilder.setEmbed(builder.build());
                    reportProcessing.setMessage(
                            channel
                                    .sendMessage(messageBuilder.build())
                                    .complete()
                    );
                    reportProcessing.next(user, (rp, hasNext) -> rp.message = rp.message.editMessage(
                            report.getAuthor(event.getJDA()).getName() +
                            ", diga o que **deveria** acontecer normalmente em poucas palavras.")
                                                                                        .complete());
                }
            } else if (componentId.startsWith("update-report")) {
                final String[] strings = componentId.split("-");
                event.getInteraction().deferEdit().queue();
                final Function<Integer, Long> parseLong = (position) -> Long.parseLong(strings[position]);
                if (componentId.startsWith("update-report-") && strings.length == 3) {
                    this.service.read(parseLong.apply(2))
                                .invoke(report -> message.editMessage(Commons.buildInfoMsgFrom(report, user).build())
                                                         .queue()).await().indefinitely();
                } else if (componentId.startsWith("update-report-status-") && strings.length == 4) {
                    this.service.read(parseLong.apply(3)).invoke(report -> {
                        switch (report.getStatus()) {
                            case ACTIVATED:
                                message
                                        .editMessage(Commons.buildInfoMsgFrom(report, user).build())
                                        .setActionRow(
                                                Button.success("update-report-status-accepted-" + report.getId(),
                                                               "Aceitar"),
                                                Button.danger("update-report-status-refused-" + report.getId(), "Recusar")
                                        ).queue();
                                break;
                            case ACCEPTED:
                                message
                                        .editMessage(Commons.buildInfoMsgFrom(report, user).build())
                                        .setActionRow(
                                                Button.secondary("update-report-status-archived-" + report.getId(),
                                                                 "Arquivar")
                                        ).queue();
                                break;
                            case ARCHIVED:
                            case REFUSED:
                                message
                                        .editMessage(Commons.buildInfoMsgFrom(report, user).build())
                                        .setActionRow(
                                                Button.secondary("update-report-status-activated-" + report.getId(),
                                                                 "Voltar a análise")
                                        ).queue();
                                break;
                        }
                    }).await().indefinitely();
                } else if (componentId.startsWith("update-report-status-")) {
                    final Long id = parseLong.apply(4);
                    switch (strings[3]) {
                        case "accepted":
                            this.updateStatus(id, ReportStatus.ACCEPTED, message);
                            break;
                        case "refused":
                            this.updateStatus(id, ReportStatus.REFUSED, message);
                            break;
                        case "archived" :
                            this.updateStatus(id, ReportStatus.ARCHIVED, message);
                            break;
                        case "activated":
                            this.updateStatus(id, ReportStatus.ACTIVATED, message);
                            break;
                    }
                }
            }
        }
    }

    private void updateStatus(long id, ReportStatus status, Message message) {
        this.service.read(id).chain(report -> {
            report.setStatus(status);
            return this.service.update(report);
        }).invoke(report -> {
            message.delete().queue();
            this.dispatcher.dispatch(ReportDispatch.fromReportStatus(status), report);
        }).await().indefinitely();
    }

}
