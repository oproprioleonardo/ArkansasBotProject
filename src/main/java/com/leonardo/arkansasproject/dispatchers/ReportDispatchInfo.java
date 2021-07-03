package com.leonardo.arkansasproject.dispatchers;

import com.google.gson.JsonObject;

import java.awt.*;

public interface ReportDispatchInfo {

    String getChannelId();

    String getRouteInConfig();

    Color getColor();

    boolean isLoaded();

    void load(JsonObject config);

    class ActivatedReport extends ReportDispatchInfoImpl {
        @Override
        public String getRouteInConfig() {
            return "ACTIVATED_REPORTS_CHANNEL";
        }

        @Override
        public Color getColor() {
            return new Color(236, 255, 43);
        }
    }

    class RefusedReport extends ReportDispatchInfoImpl {
        @Override
        public String getRouteInConfig() {
            return "REFUSED_REPORTS_CHANNEL";
        }

        @Override
        public Color getColor() {
            return new Color(201, 30, 30);
        }
    }

    class AcceptedReport extends ReportDispatchInfoImpl {
        @Override
        public String getRouteInConfig() {
            return "ACCEPTED_REPORTS_CHANNEL";
        }

        @Override
        public Color getColor() {
            return new Color(40, 239, 32);
        }
    }

    class ArchivedReport extends ReportDispatchInfoImpl {
        @Override
        public String getRouteInConfig() {
            return "ARCHIVED_REPORTS_CHANNEL";
        }

        @Override
        public Color getColor() {
            return new Color(177, 177, 177, 255);
        }
    }

}
