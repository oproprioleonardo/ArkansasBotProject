package com.leonardo.arkansasproject.dispatchers;

import com.leonardo.arkansasproject.Bot;
import lombok.Getter;

public class ReportDispatchDestinationImpl implements ReportDispatchDestination {

    @Getter
    private boolean loaded = false;
    @Getter
    private String channelId;

    @Override
    public String getRouteInConfig() {
        return "";
    }

    public void load() {
        this.channelId = Bot.getInstance().getConfig().get(getRouteInConfig()).getAsString();
        this.loaded = true;
    }
}