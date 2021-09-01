package com.leonardo.arkansasproject.dispatchers;

import io.github.cdimascio.dotenv.Dotenv;

import java.awt.*;

public interface ReportDispatchInfo {

    static ReportDispatchInfo asActivatedReport() {
        return new ReportDispatchInfoImpl("ACTIVATED_REPORTS_CHANNEL", new Color(255, 255, 101));
    }

    static ReportDispatchInfo asRefusedReport() {
        return new ReportDispatchInfoImpl("REFUSED_REPORTS_CHANNEL", new Color(212, 105, 105));
    }

    static ReportDispatchInfo asAcceptedReport() {
        return new ReportDispatchInfoImpl("ACCEPTED_REPORTS_CHANNEL", new Color(100, 236, 113));
    }

    static ReportDispatchInfo asArchivedReport() {
        return new ReportDispatchInfoImpl("ARCHIVED_REPORTS_CHANNEL", new Color(177, 177, 177, 255));
    }

    String getChannelId();

    String getRouteInConfig();

    Color getColorMessage();

    void load(Dotenv dotenv);

}
