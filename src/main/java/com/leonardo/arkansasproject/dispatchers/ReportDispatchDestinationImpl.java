package com.leonardo.arkansasproject.dispatchers;

import com.google.gson.JsonObject;
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

    public void load(JsonObject config) {
        this.channelId = config.get(getRouteInConfig()).getAsString();
        this.loaded = true;
    }
}
