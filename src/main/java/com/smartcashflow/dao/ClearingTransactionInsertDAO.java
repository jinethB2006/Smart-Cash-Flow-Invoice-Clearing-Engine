package com.smartcashflow.dao;

import com.smartcashflow.model.ClearingTransaction;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class ClearingTransactionInsertDAO {

    public void insertTransaction(
            Connection connection,
            ClearingTransaction transaction
    ) throws Exception {

        String sql =
                "INSERT INTO clearing_transactions " +
                "(payment_id, invoice_id, action, amount, description) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(
                    1,
                    transaction.getPaymentId()
            );

            statement.setString(
                    2,
                    transaction.getInvoiceId()
            );

            statement.setString(
                    3,
                    transaction.getAction()
            );

            statement.setBigDecimal(
                    4,
                    transaction.getAmount()
            );

            statement.setString(
                    5,
                    transaction.getDescription()
            );

            statement.executeUpdate();
        }
    }
}