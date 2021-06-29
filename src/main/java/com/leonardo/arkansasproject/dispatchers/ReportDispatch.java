package com.leonardo.arkansasproject.dispatchers;

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

    public ReportDispatchDestination getInstance() {
        if (!reportDispatchDestination.isLoaded()) reportDispatchDestination.load();
        return reportDispatchDestination;
    }

    public static ReportDispatch fromReportStatus(ReportStatus reportStatus) {
        switch (reportStatus) {
            case ACCEPTED: return ACCEPTED;
            case REFUSED: return REFUSED;
            case ARCHIVED: return ARCHIVED;
            default: return ACTIVATED;
        }
    }
}
