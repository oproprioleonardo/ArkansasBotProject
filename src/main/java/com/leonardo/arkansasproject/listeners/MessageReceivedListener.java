package com.leonardo.arkansasproject.listeners;

import com.google.inject.Inject;
import com.leonardo.arkansasproject.Bot;
import com.leonardo.arkansasproject.models.Report;
import com.leonardo.arkansasproject.models.suppliers.ReportProcessing;
import com.leonardo.arkansasproject.utils.Checker;
import com.leonardo.arkansasproject.utils.TemplateMessages;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class MessageReceivedListener implements EventListener {

    @Inject
    private Bot bot;

    @Override
    public void onEvent(@NotNull GenericEvent genericEvent) {
        if (genericEvent instanceof MessageReceivedEvent) {
            final MessageReceivedEvent event = (MessageReceivedEvent) genericEvent;
            final Message eventMessage = event.getMessage();
            final String contentRaw = eventMessage.getContentRaw();
            if (Checker.isBotCommand(contentRaw.split(" ")[0])) return;
            if (eventMessage.isWebhookMessage()) return;
            final User author = event.getAuthor();
            this.bot.REPORT_PROCESSING.forEach(entry -> {
                final ReportProcessing reportProcessing = entry.getValue();
                final MessageChannel channel = eventMessage.getChannel();
                if (entry.getKey().equals(author.getId()) &&
                    channel.getIdLong() == event.getChannel().getIdLong()) {
                    final Report report = reportProcessing.getReport();
                    if (eventMessage.isFromGuild()) eventMessage.delete().queue();
                    switch (reportProcessing.getProcessingState()) {
                        case ATTACH_STEP_BY_STEP:
                            if (!contentRaw.equalsIgnoreCase("pronto")) {
                                Arrays.stream(contentRaw.split("\n")).forEach(s -> {
                                    if (!s.isEmpty()) report.appendStep(s);
                                });
                                reportProcessing.updateAllFields();
                                reportProcessing.updateMessage();
                                break;
                            }
                            reportProcessing.next();
                            break;
                        case ATTACH_EXPECTED_RESULT:
                            if (!Checker.characterLength(contentRaw, 60)) {
                                channel.sendMessage(TemplateMessages.ARGS_LENGTH_NOT_SUPPORTED.getMessageEmbed())
                                       .complete().delete().delay(20, TimeUnit.SECONDS).queue();
                                return;
                            }
                            report.setExpectedOutcome(contentRaw);
                            reportProcessing.next();
                            break;
                        case ATTACH_ACTUAL_RESULT:
                            if (!Checker.characterLength(contentRaw, 60)) {
                                channel.sendMessage(TemplateMessages.ARGS_LENGTH_NOT_SUPPORTED.getMessageEmbed())
                                       .complete().delete().delay(20, TimeUnit.SECONDS).queue();
                                return;
                            }
                            report.setActualResult(contentRaw);
                            reportProcessing.next();
                            break;
                        case ATTACH_SERVER:
                            report.setServerName(contentRaw);
                            reportProcessing.next();
                            break;
                    }
                }
            });
        }
    }
}
