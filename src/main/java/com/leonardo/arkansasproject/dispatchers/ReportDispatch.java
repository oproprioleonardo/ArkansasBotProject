package com.leonardo.arkansasproject.dispatchers;

import com.google.gson.JsonObject;
import com.leonardo.arkansasproject.models.ReportStatus;

public enum ReportDispatch {

    ACTIVATED(new ReportDispatchDestination.ActivatedReport()),
    ACCEPTED(new ReportDispatchDestination.AcceptedReport()),
    ARCHIVED(new ReportDispatchDestination.ArchivedReport()),
    REFUSED(new ReportDispatchDestination.RefusedReport());

    private final ReportDispatchDestination reportDispatchDestination;

    ReportDispatch(ReportDispatchDestination reportDispatchDestination) {
        this.reportDispatchDestination = reportDispatchDestination;
    }

    public static ReportDispatch fromReportStatus(ReportStatus reportStatus) {
        return reportStatus != null ? reportStatus : ACTIVATED;
    }

    public ReportDispatchDestination getInstance(JsonObject config) {
        if (!reportDispatchDestination.isLoaded()) reportDispatchDestination.load(config);
        return reportDispatchDestination;
    }
}
