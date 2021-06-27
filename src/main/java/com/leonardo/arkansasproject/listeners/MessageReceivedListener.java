package com.leonardo.arkansasproject.listeners;

import com.google.inject.Inject;
import com.leonardo.arkansasproject.Bot;
import com.leonardo.arkansasproject.models.Report;
import com.leonardo.arkansasproject.models.suppliers.ReportProcessing;
import com.leonardo.arkansasproject.utils.Checker;
import com.leonardo.arkansasproject.utils.TemplateMessages;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.ehcache.Cache;

import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class MessageReceivedListener extends ListenerAdapter {

    @Inject
    private Bot bot;

    @Override
    public void onButtonClick(ButtonClickEvent event) {
        if (event.getComponentId().equals("confirm-next")) {
            final Cache<String, ReportProcessing> cache = this.bot.REPORT_PROCESSING;
            final ReportProcessing reportProcessing = cache.get(event.getUser().getId());
            final Report report = reportProcessing.getReport();
            if (report.getSteps().isEmpty()) {
                event.getChannel().sendMessage(TemplateMessages.NO_STEPS.getMessageEmbed()).complete().delete()
                     .queueAfter(12, TimeUnit.SECONDS);
                return;
            }
            Objects.requireNonNull(event.getMessage()).delete().queue();

            final EmbedBuilder builder = TemplateMessages.TEMPLATE_PROCESSING_REPORT.getEmbedBuilder();
            final MessageBuilder messageBuilder = new MessageBuilder();

            builder.setAuthor(report.getAuthor().getAsTag() + " (" + report.getUserId() + ")");
            builder
                    .appendDescription("[")
                    .appendDescription(report.getTitle())
                    .appendDescription("](https://github.com/LeonardoCod3r) \n")
                    .appendDescription("\n");
            messageBuilder.setEmbed(builder.build());
            reportProcessing.setMessage(
                    event.getChannel()
                         .sendMessage(messageBuilder.build())
                         .complete()
            );
            reportProcessing.next();
        }
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        final Message eventMessage = event.getMessage();
        final String contentRaw = eventMessage.getContentRaw();
        if (Checker.isBotCommand(contentRaw.split(" ")[0])) return;
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
                        Arrays.stream(contentRaw.split("\n")).forEach(s -> {
                            if (!s.isEmpty()) report.appendStep(s);
                        });
                        reportProcessing.updateAllFields();
                        reportProcessing.updateMessage();
                        break;
                    case ATTACH_EXPECTED_RESULT:
                        if (!Checker.characterLength(contentRaw, 60)) {
                            channel.sendMessage(TemplateMessages.TEXT_LENGTH_NOT_SUPPORTED.getMessageEmbed())
                                   .complete().delete().queueAfter(12, TimeUnit.SECONDS);
                            return;
                        }
                        report.setExpectedOutcome(contentRaw);
                        reportProcessing.next();
                        break;
                    case ATTACH_ACTUAL_RESULT:
                        if (!Checker.characterLength(contentRaw, 60)) {
                            channel.sendMessage(TemplateMessages.TEXT_LENGTH_NOT_SUPPORTED.getMessageEmbed())
                                   .complete().delete().queueAfter(12, TimeUnit.SECONDS);
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
