package com.smartcashflow.service;

import com.smartcashflow.dao.PaymentAllocationDAO;
import com.smartcashflow.model.PaymentAllocation;

import java.util.List;
import java.util.stream.Collectors;

public class PaymentAllocationService {

    private final PaymentAllocationDAO allocationDAO;

    public PaymentAllocationService() {
        this.allocationDAO = new PaymentAllocationDAO();
    }

    public List<PaymentAllocation> getAllAllocations() {

        return allocationDAO.getAllAllocations();
    }

    public List<PaymentAllocation> getPaymentAllocations(
            String paymentId) {

        return allocationDAO.getAllAllocations()
                .stream()
                .filter(allocation ->
                        allocation.getPaymentId().equals(paymentId)
                )
                .collect(Collectors.toList());
    }

    public List<PaymentAllocation> getInvoiceAllocations(
            String invoiceId) {

        return allocationDAO.getAllAllocations()
                .stream()
                .filter(allocation ->
                        allocation.getInvoiceId().equals(invoiceId)
                )
                .collect(Collectors.toList());
    }
}