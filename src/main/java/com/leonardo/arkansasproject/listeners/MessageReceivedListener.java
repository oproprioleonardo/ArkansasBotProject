package com.leonardo.arkansasproject.listeners;

import com.google.inject.Inject;
import com.leonardo.arkansasproject.dispatchers.Dispatcher;
import com.leonardo.arkansasproject.dispatchers.ReportDispatch;
import com.leonardo.arkansasproject.managers.ReportProcessingManager;
import com.leonardo.arkansasproject.models.Report;
import com.leonardo.arkansasproject.models.ReportProcessing;
import com.leonardo.arkansasproject.services.ReportService;
import com.leonardo.arkansasproject.validators.TextValidator;
import com.leonardo.arkansasproject.utils.TemplateMessages;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

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
        if (TextValidator.isBotCommand(contentRaw.split(" ")[0])) return;
        final User author = event.getAuthor();
        final MessageChannel channel = eventMessage.getChannel();
        if (this.manager.exists(author.getIdLong()) && channel.getIdLong() == event.getChannel().getIdLong()) {
            final ReportProcessing reportProcessing = this.manager.get(author.getIdLong());
            final Report report = reportProcessing.getReport();
            if (eventMessage.isFromGuild()) eventMessage.delete().queue();
            switch (reportProcessing.getProcessingState()) {
                case ATTACH_STEP_BY_STEP:
                    if (!TextValidator.characterLength(contentRaw, 80)) {
                        channel.sendMessage(TemplateMessages.TEXT_LENGTH_NOT_SUPPORTED.getMessageEmbed())
                               .complete().delete().queueAfter(12, TimeUnit.SECONDS);
                        return;
                    }
                    Arrays.stream(contentRaw.split("\n")).forEach(s -> {
                        if (!s.isEmpty()) report.appendStep(s);
                    });
                    reportProcessing.updateMessage(author);
                    break;
                case ATTACH_EXPECTED_RESULT:
                    if (!TextValidator.characterLength(contentRaw, 60)) {
                        channel.sendMessage(TemplateMessages.TEXT_LENGTH_NOT_SUPPORTED.getMessageEmbed())
                               .complete().delete().queueAfter(12, TimeUnit.SECONDS);
                        return;
                    }
                    report.setExpectedOutcome(contentRaw);
                    reportProcessing.next(author, (rp, aBoolean) -> rp.message = rp.message.editMessage(
                            author.getName() + ", diga o que **realmente** aconteceu em poucas palavras.")
                                                                                           .complete());
                    break;
                case ATTACH_ACTUAL_RESULT:
                    if (!TextValidator.characterLength(contentRaw, 60)) {
                        channel.sendMessage(TemplateMessages.TEXT_LENGTH_NOT_SUPPORTED.getMessageEmbed())
                               .complete().delete().queueAfter(12, TimeUnit.SECONDS);
                        return;
                    }
                    report.setActualResult(contentRaw);
                    reportProcessing.next(author, (rp, b) -> rp.message = rp.message.editMessage(
                            author.getName() + ", diga em qual servidor o bug ocorreu.").complete());
                    break;
                case ATTACH_SERVER:
                    report.setServerName(contentRaw);
                    reportProcessing.next(author, (rp, b) -> this.service
                            .create(report).invoke(() -> {
                                                       channel.sendMessage(TemplateMessages.REPORT_SUCCESS.getMessageEmbed()).queue();
                                                       channel.sendMessage(rp.buildMessage(author))
                                                              .queue((msg) -> rp.message.delete().queue());
                                                       this.manager.remove(author.getIdLong());
                                                       this.dispatcher.dispatch(ReportDispatch.ACTIVATED, report);
                                                   }
                            ).await().indefinitely());
                    break;
            }
        }
    }
}
