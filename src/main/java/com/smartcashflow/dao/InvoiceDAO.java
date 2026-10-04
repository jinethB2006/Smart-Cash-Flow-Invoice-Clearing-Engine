package com.smartcashflow.dao;

import com.smartcashflow.model.Invoice;
import com.smartcashflow.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class InvoiceDAO {

    public List<Invoice> getAllInvoices() {

        List<Invoice> invoices = new ArrayList<>();

        String sql =
                "SELECT invoice_id, " +
                "customer_id, " +
                "invoice_date, " +
                "due_date, " +
                "invoice_amount, " +
                "paid_amount, " +
                "status " +
                "FROM invoices";

        try (
                Connection connection = DBConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()
        ) {

            while (resultSet.next()) {

                Invoice invoice = new Invoice();

                invoice.setInvoiceId(
                        resultSet.getString("invoice_id")
                );

                invoice.setCustomerId(
                        resultSet.getString("customer_id")
                );

                invoice.setInvoiceDate(
                        resultSet.getDate("invoice_date").toLocalDate()
                );

                invoice.setDueDate(
                        resultSet.getDate("due_date").toLocalDate()
                );

                invoice.setInvoiceAmount(
                        resultSet.getBigDecimal("invoice_amount")
                );

                invoice.setPaidAmount(
                        resultSet.getBigDecimal("paid_amount")
                );

                invoice.setStatus(
                        resultSet.getString("status")
                );

                invoices.add(invoice);
            }

        } catch (Exception e) {

            e.printStackTrace();
        }

        return invoices;
    }
}