package com.smartcashflow.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String URL =
            "jdbc:mysql://localhost:3306/smart_cash_flow";

    private static final String USER =
            System.getenv().getOrDefault("SMART_CASH_FLOW_DB_USER", "root");

    private static final String PASSWORD =
            System.getenv("SMART_CASH_FLOW_DB_PASSWORD");

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(
                    "MySQL JDBC Driver not found",
                    e
            );
        }
    }

    public static Connection getConnection()
            throws SQLException {

        if (PASSWORD == null || PASSWORD.isEmpty()) {
            throw new SQLException(
                    "SMART_CASH_FLOW_DB_PASSWORD environment variable is not set"
            );
        }

        return DriverManager.getConnection(
                URL,
                USER,
                PASSWORD
        );
    }
}