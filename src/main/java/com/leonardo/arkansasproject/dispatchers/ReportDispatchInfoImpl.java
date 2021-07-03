package com.leonardo.arkansasproject.dispatchers;

import com.google.gson.JsonObject;
import lombok.Getter;

import java.awt.*;

public abstract class ReportDispatchInfoImpl implements ReportDispatchInfo {

    @Getter
    private boolean loaded = false;
    @Getter
    private String channelId;

    @Override
    public Color getColor() {
        return new Color(59, 56, 209);
    }

    @Override
    public String getRouteInConfig() {
        return "";
    }

    public void load(JsonObject config) {
        this.channelId = config.get(getRouteInConfig()).getAsString();
        this.loaded = true;
    }
}
