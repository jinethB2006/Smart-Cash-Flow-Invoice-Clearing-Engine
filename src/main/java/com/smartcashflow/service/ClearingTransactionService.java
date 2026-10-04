package com.smartcashflow.service;

import com.smartcashflow.dao.ClearingTransactionDAO;
import com.smartcashflow.model.ClearingTransaction;

import java.util.List;
import java.util.stream.Collectors;

public class ClearingTransactionService {

    private final ClearingTransactionDAO transactionDAO;

    public ClearingTransactionService() {
        this.transactionDAO = new ClearingTransactionDAO();
    }

    public List<ClearingTransaction> getAllTransactions() {

        return transactionDAO.getAllTransactions();
    }

    public List<ClearingTransaction> getPaymentTransactions(
            String paymentId) {

        return transactionDAO.getAllTransactions()
                .stream()
                .filter(transaction ->
                        transaction.getPaymentId() != null &&
                        transaction.getPaymentId().equals(paymentId)
                )
                .collect(Collectors.toList());
    }

    public List<ClearingTransaction> getInvoiceTransactions(
            String invoiceId) {

        return transactionDAO.getAllTransactions()
                .stream()
                .filter(transaction ->
                        transaction.getInvoiceId() != null &&
                        transaction.getInvoiceId().equals(invoiceId)
                )
                .collect(Collectors.toList());
    }
}