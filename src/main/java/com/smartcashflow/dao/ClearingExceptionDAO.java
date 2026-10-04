package com.smartcashflow.dao;

import com.smartcashflow.model.ClearingException;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class ClearingExceptionDAO {

    public void insertException(
            Connection connection,
            ClearingException exception
    ) throws Exception {

        String sql =
                "INSERT INTO clearing_exceptions " +
                "(payment_id, customer_id, amount, reason) " +
                "VALUES (?, ?, ?, ?)";

        try (
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(
                    1,
                    exception.getPaymentId()
            );

            statement.setString(
                    2,
                    exception.getCustomerId()
            );

            statement.setBigDecimal(
                    3,
                    exception.getAmount()
            );

            statement.setString(
                    4,
                    exception.getReason()
            );

            statement.executeUpdate();
        }
    }
}