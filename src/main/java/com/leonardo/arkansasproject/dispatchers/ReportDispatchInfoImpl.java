package com.leonardo.arkansasproject.dispatchers;

import com.google.gson.JsonObject;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

@Getter
@Setter
@NoArgsConstructor
public class ReportDispatchInfoImpl implements ReportDispatchInfo {

    @Setter(value = AccessLevel.NONE)
    private boolean loaded = false;
    private String channelId;
    private String routeInConfig;
    private Color colorMessage = new Color(59, 56, 209);

    public ReportDispatchInfoImpl(@NotNull String routeInConfig, @NotNull Color colorMessage) {
        this.routeInConfig = routeInConfig;
        this.colorMessage = colorMessage;
    }

    public void load(JsonObject config) {
        this.channelId = config.get(getRouteInConfig()).getAsString();
        this.loaded = true;
    }
}
