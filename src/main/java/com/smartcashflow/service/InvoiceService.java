package com.smartcashflow.service;

import com.smartcashflow.dao.InvoiceDAO;
import com.smartcashflow.model.Invoice;

import java.util.List;
import java.util.stream.Collectors;

public class InvoiceService {

    private final InvoiceDAO invoiceDAO;

    public InvoiceService() {
        this.invoiceDAO = new InvoiceDAO();
    }

    public List<Invoice> getPendingInvoices() {

        return invoiceDAO.getAllInvoices()
                .stream()
                .filter(invoice ->
                        "PENDING".equals(invoice.getStatus()) ||
                        "PARTIALLY_PAID".equals(invoice.getStatus())
                )
                .collect(Collectors.toList());
    }

    public List<Invoice> getCustomerInvoices(String customerId) {

        return invoiceDAO.getAllInvoices()
                .stream()
                .filter(invoice ->
                        invoice.getCustomerId().equals(customerId)
                )
                .collect(Collectors.toList());
    }
}