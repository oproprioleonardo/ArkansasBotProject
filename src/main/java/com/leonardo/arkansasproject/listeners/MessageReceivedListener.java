package com.leonardo.arkansasproject.listeners;

import com.google.inject.Inject;
import com.leonardo.arkansasproject.Bot;
import com.leonardo.arkansasproject.models.Report;
import com.leonardo.arkansasproject.models.suppliers.ReportProcessing;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class MessageReceivedListener implements EventListener {

    @Inject
    private Bot bot;

    @Override
    public void onEvent(@NotNull GenericEvent genericEvent) {
        if (genericEvent instanceof MessageReceivedEvent) {
            final MessageReceivedEvent event = (MessageReceivedEvent) genericEvent;
            final Message eventMessage = event.getMessage();
            if(eventMessage.isFromGuild() || eventMessage.isWebhookMessage()) return;
            final User author = event.getAuthor();
            this.bot.REPORT_PROCESSING.forEach(entry -> {
                final String userId = entry.getKey();
                final ReportProcessing reportProcessing = entry.getValue();
                final Message message = reportProcessing.getMessage();
                if (userId.equals(author.getId()) && message.getChannel().getIdLong() == event.getChannel().getIdLong()) {

                        final EmbedBuilder builder = new EmbedBuilder();
                        final Report report = reportProcessing.getReport();
                        builder.setColor(new Color(59, 56, 209));
                        builder.setAuthor(author.getAsTag() + " (" + author.getId() + ")");
                        builder.getDescriptionBuilder().insert(0,  "[" + report.getTitle() + "](https://github.com/LeonardoCod3r)");
                        switch (reportProcessing.getProcessingState()) {
                            case ATTACH_STEP_BY_STEP:
                                if (eventMessage.getContentRaw().equals("pronto")) {

                                } else {

                                }
                                break;
                        }
                }
            });
        }
    }
}
