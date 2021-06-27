package com.leonardo.arkansasproject.dispatchers;

public interface ReportDispatchDestination {

    String getChannelId();
    String getRouteInConfig();
    boolean isLoaded();
    void load();

    class ActivatedReport extends ReportDispatchDestinationImpl {
        @Override
        public String getRouteInConfig() {
            return "ACTIVE_REPORTS_CHANNEL";
        }
    }

    class AcceptedReport extends ReportDispatchDestinationImpl {
        @Override
        public String getRouteInConfig() {
            return "ACCEPTED_REPORTS_CHANNEL";
        }
    }

    class ArchivedReport extends ReportDispatchDestinationImpl {
        @Override
        public String getRouteInConfig() {
            return "ARCHIVED_REPORTS_CHANNEL";
        }
    }

}