package com.smartcashflow.dao;

import com.smartcashflow.model.ClearingTransaction;
import com.smartcashflow.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ClearingTransactionDAO {

    public List<ClearingTransaction> getAllTransactions() {

        List<ClearingTransaction> transactions = new ArrayList<>();

        String sql =
                "SELECT transaction_id, " +
                "payment_id, " +
                "invoice_id, " +
                "action, " +
                "amount, " +
                "description, " +
                "created_at " +
                "FROM clearing_transactions";

        try (
                Connection connection = DBConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()
        ) {

            while (resultSet.next()) {

                ClearingTransaction transaction =
                        new ClearingTransaction();

                transaction.setTransactionId(
                        resultSet.getLong("transaction_id")
                );

                transaction.setPaymentId(
                        resultSet.getString("payment_id")
                );

                transaction.setInvoiceId(
                        resultSet.getString("invoice_id")
                );

                transaction.setAction(
                        resultSet.getString("action")
                );

                transaction.setAmount(
                        resultSet.getBigDecimal("amount")
                );

                transaction.setDescription(
                        resultSet.getString("description")
                );

                if (resultSet.getTimestamp("created_at") != null) {
                    transaction.setCreatedAt(
                            resultSet.getTimestamp("created_at")
                                    .toLocalDateTime()
                    );
                }

                transactions.add(transaction);
            }

        } catch (Exception e) {

            e.printStackTrace();
        }

        return transactions;
    }
}