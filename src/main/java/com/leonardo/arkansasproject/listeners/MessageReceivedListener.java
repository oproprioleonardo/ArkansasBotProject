package com.leonardo.arkansasproject.listeners;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.leonardo.arkansasproject.dispatchers.Dispatcher;
import com.leonardo.arkansasproject.dispatchers.ReportDispatch;
import com.leonardo.arkansasproject.entities.Report;
import com.leonardo.arkansasproject.managers.ReportProcessingManager;
import com.leonardo.arkansasproject.report.ReportProcessing;
import com.leonardo.arkansasproject.services.ReportService;
import com.leonardo.arkansasproject.utils.Commons;
import com.leonardo.arkansasproject.utils.TemplateMessage;
import com.leonardo.arkansasproject.validators.Validators;
import com.leonardo.arkansasproject.validators.exceptions.ArkansasException;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.Button;

import java.util.Arrays;

@Singleton
public class MessageReceivedListener extends ListenerAdapter {

    @Inject
    private ReportProcessingManager manager;
    @Inject
    private ReportService service;
    @Inject
    private Dispatcher dispatcher;

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        final Message eventMessage = event.getMessage();
        final String contentRaw = eventMessage.getContentRaw();
        if (Validators.isBotCommand(contentRaw.split(" ")[0])) return;
        final User author = event.getAuthor();
        final MessageChannel channel = eventMessage.getChannel();
        if (this.manager.exists(author.getIdLong())) {
            final ReportProcessing repProcess = this.manager.get(author.getIdLong());
            if (channel.getIdLong() != repProcess.message.getChannel().getIdLong()) return;
            final Report report = repProcess.getReport();
            if (eventMessage.isFromGuild()) eventMessage.delete().queue();
            switch (repProcess.getProcessingState()) {
                case ATTACH_STEP_BY_STEP:
                    try {
                        Validators.hasCharLenghtOrThrow(contentRaw, 600);
                    } catch (ArkansasException e) {
                        e.throwMessage(channel);
                        return;
                    }
                    Arrays.stream(contentRaw.split("\n")).forEach(s -> {
                        if (!s.isEmpty()) report.appendStep(s);
                    });
                    repProcess.updateMessage(author);
                    break;
                case ATTACH_EXPECTED_RESULT:
                    try {
                        Validators.hasCharLenghtOrThrow(contentRaw, 80);
                    } catch (ArkansasException e) {
                        e.throwMessage(channel);
                        return;
                    }
                    report.setExpectedOutcome(contentRaw);
                    repProcess.next(author, (rp, aBoolean) -> rp.message = rp.message.editMessage(
                            "**" + author.getName() +
                            "**, agora informe-nos o que realmente acontece, ou seja, o resultado real.")
                                                                                     .complete());
                    break;
                case ATTACH_ACTUAL_RESULT:
                    try {
                        Validators.hasCharLenghtOrThrow(contentRaw, 80);
                    } catch (ArkansasException e) {
                        e.throwMessage(channel);
                        return;
                    }
                    report.setActualResult(contentRaw);
                    repProcess.next(author, (rp, b) -> rp.message = rp.message.editMessage(
                            "**" + author.getName() +
                            "**, estamos já terminando! Para enviarmos o bug para análise precisamos de uma última informação!\n" +
                            "Informe-nos o servidor onde ocorreu o bug, tanto pode ser, \"Bedwars\" como uma sala específica, ou seja, \"Bedwars-01\".")
                                                                              .complete());
                    break;
                case ATTACH_SERVER:
                    try {
                        Validators.hasCharLenghtOrThrow(contentRaw, 3, 30);
                    } catch (ArkansasException e) {
                        e.throwMessage(channel);
                        return;
                    }
                    report.setServerName(contentRaw);
                    repProcess.next(author, (rp, b) -> this.service
                            .create(report).invoke(() -> {
                                                       rp.message.delete().queue();
                                                       this.manager.remove(author.getIdLong());
                                                       author.openPrivateChannel().queue(privateChannel -> {
                                                           privateChannel.sendMessageEmbeds(TemplateMessage.REPORT_SUCCESS.getMessageEmbed())
                                                                         .queue();
                                                           privateChannel.sendMessage(Commons.buildInfoMsgFrom(report, author, author).build())
                                                                         .setActionRow(
                                                                                 Button.success("update-report-" + report.getId(), "Atualizar")
                                                                         ).queue();
                                                       });
                                                       this.dispatcher.dispatch(ReportDispatch.ACTIVATED, report);
                                                   }
                            ).await().indefinitely());
                    break;
            }
        }
    }
}
