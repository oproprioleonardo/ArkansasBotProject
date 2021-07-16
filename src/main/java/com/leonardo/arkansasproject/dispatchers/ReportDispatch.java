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
        return reportStatus != null ? reportStatus : ACTIVATED;
    }

    public ReportDispatchInfo getInfo() {
        return reportDispatchInfo;
    }
}
