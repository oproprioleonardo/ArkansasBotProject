package com.leonardo.arkansasproject.dispatchers;

import com.google.gson.JsonObject;

import java.awt.*;

public interface ReportDispatchInfo {

    static ReportDispatchInfo asActivatedReport() {
        return new ReportDispatchInfoImpl("ACTIVATED_REPORTS_CHANNEL", new Color(236, 255, 43));
    }

    static ReportDispatchInfo asRefusedReport() {
        return new ReportDispatchInfoImpl("REFUSED_REPORTS_CHANNEL", new Color(201, 30, 30));
    }

    static ReportDispatchInfo asAcceptedReport() {
        return new ReportDispatchInfoImpl("ACCEPTED_REPORTS_CHANNEL", new Color(40, 239, 32));
    }

    static ReportDispatchInfo asArchivedReport() {
        return new ReportDispatchInfoImpl("ARCHIVED_REPORTS_CHANNEL", new Color(177, 177, 177, 255));
    }

    String getChannelId();

    String getRouteInConfig();

    Color getColorMessage();

    boolean isLoaded();

    void load(JsonObject config);

}
