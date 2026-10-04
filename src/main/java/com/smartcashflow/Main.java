package com.smartcashflow;

import com.mysql.cj.jdbc.AbandonedConnectionCleanupThread;
import com.smartcashflow.service.ClearingReportService;

public class Main {

    public static void main(String[] args) {

        try {

            ClearingReportService reportService =
                    new ClearingReportService();

            reportService.generateReport();

        } finally {

            try {
                AbandonedConnectionCleanupThread.checkedShutdown();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}