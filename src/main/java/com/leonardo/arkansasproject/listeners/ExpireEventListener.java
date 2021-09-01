package com.leonardo.arkansasproject.listeners;

import com.google.inject.Singleton;
import com.leonardo.arkansasproject.report.ReportProcessing;
import com.leonardo.arkansasproject.utils.TemplateMessage;
import net.dv8tion.jda.api.entities.MessageChannel;
import org.ehcache.event.CacheEvent;
import org.ehcache.event.CacheEventListener;

import java.util.concurrent.TimeUnit;

@Singleton
public class ExpireEventListener implements CacheEventListener<Long, ReportProcessing> {

    @Override
    public void onEvent(CacheEvent<? extends Long, ? extends ReportProcessing> e) {
        final ReportProcessing value = e.getOldValue();
        final MessageChannel channel = value.message.getChannel();
        value.message.delete().queue();
        channel.sendMessageEmbeds(TemplateMessage.EXPIRED.getMessageEmbed()).complete().delete()
               .queueAfter(30, TimeUnit.SECONDS);
    }
}
