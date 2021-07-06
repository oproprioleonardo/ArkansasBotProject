package com.leonardo.arkansasproject.dispatchers;

import com.leonardo.arkansasproject.models.ReportStatus;

public enum ReportDispatch {

    ACTIVATED(ReportDispatchInfo.asActivatedReport()),
    ACCEPTED(ReportDispatchInfo.asAcceptedReport()),
    ARCHIVED(ReportDispatchInfo.asArchivedReport()),
    REFUSED(ReportDispatchInfo.asRefusedReport());

    private final ReportDispatchInfo reportDispatchInfo;

    ReportDispatch(ReportDispatchInfo reportDispatchInfo) {
        this.reportDispatchInfo = reportDispatchInfo;
    }

    public static ReportDispatch fromReportStatus(ReportStatus reportStatus) {
        switch (reportStatus) {
            case ACCEPTED:
                return ACCEPTED;
            case REFUSED:
                return REFUSED;
            case ARCHIVED:
                return ARCHIVED;
            default:
                return ACTIVATED;
        }
    }

    public ReportDispatchInfo getInfo() {
        return reportDispatchInfo;
    }
}
