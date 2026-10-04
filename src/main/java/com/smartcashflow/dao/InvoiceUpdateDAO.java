package com.smartcashflow.dao;

import com.smartcashflow.model.Invoice;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class InvoiceUpdateDAO {

    public void updateInvoice(
            Connection connection,
            Invoice invoice
    ) throws Exception {

        String sql =
                "UPDATE invoices " +
                "SET paid_amount = ?, status = ? " +
                "WHERE invoice_id = ?";

        try (
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setBigDecimal(
                    1,
                    invoice.getPaidAmount()
            );

            statement.setString(
                    2,
                    invoice.getStatus()
            );

            statement.setString(
                    3,
                    invoice.getInvoiceId()
            );

            statement.executeUpdate();
        }
    }
}