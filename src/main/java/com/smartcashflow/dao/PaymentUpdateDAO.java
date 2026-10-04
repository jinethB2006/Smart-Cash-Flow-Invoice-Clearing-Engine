package com.smartcashflow.dao;

import com.smartcashflow.enums.PaymentStatus;
import com.smartcashflow.model.Payment;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class PaymentUpdateDAO {

    public void updatePaymentStatus(
            Connection connection,
            Payment payment
    ) throws Exception {

        String sql =
                "UPDATE payments " +
                "SET status = ? " +
                "WHERE payment_id = ?";

        try (
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(
                    1,
                    payment.getStatus().name()
            );

            statement.setString(
                    2,
                    payment.getPaymentId()
            );

            statement.executeUpdate();
        }
    }
}