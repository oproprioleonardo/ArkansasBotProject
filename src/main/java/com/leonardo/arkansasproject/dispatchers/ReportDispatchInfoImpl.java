package com.leonardo.arkansasproject.dispatchers;

import io.github.cdimascio.dotenv.Dotenv;
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
    private String channelId;
    private String routeInConfig;
    private Color colorMessage = new Color(102, 180, 241);

    public ReportDispatchInfoImpl(@NotNull String routeInConfig, @NotNull Color colorMessage) {
        this.routeInConfig = routeInConfig;
        this.colorMessage = colorMessage;
    }

    public void load(Dotenv dotenv) {
        this.channelId = dotenv.get(getRouteInConfig());
    }
}
