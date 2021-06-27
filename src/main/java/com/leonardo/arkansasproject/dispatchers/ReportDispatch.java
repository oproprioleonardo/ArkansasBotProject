package com.leonardo.arkansasproject.dispatchers;

public enum ReportDispatch {

    ACTIVATED(new ReportDispatchDestination.ActivatedReport()),
    ACCEPTED(new ReportDispatchDestination.ActivatedReport()),
    ARCHIVED(new ReportDispatchDestination.ArchivedReport());

    private final ReportDispatchDestination reportDispatchDestination;

    ReportDispatch(ReportDispatchDestination reportDispatchDestination) {
        this.reportDispatchDestination = reportDispatchDestination;
    }

    public ReportDispatchDestination getInstance() {
        if (!reportDispatchDestination.isLoaded()) reportDispatchDestination.load();
        return reportDispatchDestination;
    }
}
