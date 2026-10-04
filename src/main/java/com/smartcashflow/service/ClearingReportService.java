package com.smartcashflow.service;

import com.smartcashflow.dao.ClearingTransactionDAO;
import com.smartcashflow.dao.InvoiceDAO;
import com.smartcashflow.dao.PaymentDAO;
import com.smartcashflow.model.ClearingTransaction;
import com.smartcashflow.model.Invoice;
import com.smartcashflow.model.Payment;

import java.math.BigDecimal;
import java.util.List;

public class ClearingReportService {

    private final PaymentDAO paymentDAO;
    private final InvoiceDAO invoiceDAO;
    private final ClearingTransactionDAO transactionDAO;

    public ClearingReportService() {

        this.paymentDAO = new PaymentDAO();
        this.invoiceDAO = new InvoiceDAO();
        this.transactionDAO = new ClearingTransactionDAO();
    }

    public void generateReport() {

        List<Payment> payments =
                paymentDAO.getAllPayments();

        List<Invoice> invoices =
                invoiceDAO.getAllInvoices();

        List<ClearingTransaction> transactions =
                transactionDAO.getAllTransactions();

        BigDecimal totalPayments =
                payments.stream()
                        .map(Payment::getPaymentAmount)
                        .reduce(
                                BigDecimal.ZERO,
                                BigDecimal::add
                        );

        BigDecimal totalInvoices =
                invoices.stream()
                        .map(Invoice::getInvoiceAmount)
                        .reduce(
                                BigDecimal.ZERO,
                                BigDecimal::add
                        );

        BigDecimal totalPaid =
                invoices.stream()
                        .map(Invoice::getPaidAmount)
                        .reduce(
                                BigDecimal.ZERO,
                                BigDecimal::add
                        );

        long paidInvoices =
                invoices.stream()
                        .filter(invoice ->
                                "PAID".equals(invoice.getStatus())
                        )
                        .count();

        long partialInvoices =
                invoices.stream()
                        .filter(invoice ->
                                "PARTIALLY_PAID".equals(
                                        invoice.getStatus()
                                )
                        )
                        .count();

        long overdueInvoices =
                invoices.stream()
                        .filter(invoice ->
                                "OVERDUE".equals(
                                        invoice.getStatus()
                                )
                        )
                        .count();

        System.out.println();
        System.out.println("======================================");
        System.out.println("       SMART CASH FLOW REPORT");
        System.out.println("======================================");

        System.out.println(
                "Total Payments       : " +
                totalPayments
        );

        System.out.println(
                "Total Invoice Value  : " +
                totalInvoices
        );

        System.out.println(
                "Total Paid Amount    : " +
                totalPaid
        );

        System.out.println(
                "Paid Invoices        : " +
                paidInvoices
        );

        System.out.println(
                "Partial Invoices     : " +
                partialInvoices
        );

        System.out.println(
                "Overdue Invoices     : " +
                overdueInvoices
        );

        System.out.println(
                "Clearing Transactions: " +
                transactions.size()
        );

        System.out.println("======================================");
    }
}