package com.smartcashflow.service;

import com.smartcashflow.dao.InvoiceDAO;
import com.smartcashflow.dao.InvoiceUpdateDAO;
import com.smartcashflow.model.Invoice;
import com.smartcashflow.util.DBConnection;

import java.sql.Connection;
import java.time.LocalDate;
import java.util.List;

public class InvoiceStatusService {

    private final InvoiceDAO invoiceDAO;
    private final InvoiceUpdateDAO invoiceUpdateDAO;

    public InvoiceStatusService() {

        this.invoiceDAO = new InvoiceDAO();
        this.invoiceUpdateDAO = new InvoiceUpdateDAO();
    }

    public void updateOverdueInvoices() {

        List<Invoice> invoices =
                invoiceDAO.getAllInvoices();

        LocalDate today =
                LocalDate.now();

        try (
                Connection connection =
                        DBConnection.getConnection()
        ) {

            connection.setAutoCommit(false);

            for (Invoice invoice : invoices) {

                if (
                        invoice.getDueDate().isBefore(today) &&
                        invoice.getRemainingAmount()
                                .compareTo(
                                        java.math.BigDecimal.ZERO
                                ) > 0 &&
                        !"CANCELLED".equals(invoice.getStatus())
                ) {

                    invoice.setStatus("OVERDUE");

                    invoiceUpdateDAO.updateInvoice(
                            connection,
                            invoice
                    );

                    System.out.println(
                            "Invoice marked OVERDUE: " +
                            invoice.getInvoiceId()
                    );
                }
            }

            connection.commit();

        } catch (Exception e) {

            e.printStackTrace();
        }
    }
}