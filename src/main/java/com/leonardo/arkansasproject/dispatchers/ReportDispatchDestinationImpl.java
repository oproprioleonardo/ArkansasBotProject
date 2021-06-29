package com.leonardo.arkansasproject.dispatchers;

import com.leonardo.arkansasproject.Bot;
import lombok.Getter;

public class ReportDispatchDestinationImpl implements ReportDispatchDestination {

    private static final Bot bot;

    static {
        bot = Bot.getInstance();
    }

    @Getter
    private boolean loaded = false;
    @Getter
    private String channelId;

    @Override
    public String getRouteInConfig() {
        return "";
    }

    public void load() {
        this.channelId = bot.getConfig().get(getRouteInConfig()).getAsString();
        this.loaded = true;
    }
}
