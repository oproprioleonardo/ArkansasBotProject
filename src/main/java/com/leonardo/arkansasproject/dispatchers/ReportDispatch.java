package com.leonardo.arkansasproject.dispatchers;

import com.leonardo.arkansasproject.report.ReportStatus;

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
        return reportStatus != null ? ReportDispatch.valueOf(reportStatus.toString()) : ACTIVATED;
    }

    public ReportDispatchInfo getInfo() {
        return reportDispatchInfo;
    }
}
