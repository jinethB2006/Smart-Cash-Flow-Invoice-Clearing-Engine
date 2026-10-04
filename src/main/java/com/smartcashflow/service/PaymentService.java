package com.smartcashflow.service;

import com.smartcashflow.dao.PaymentDAO;
import com.smartcashflow.model.Payment;

import java.util.List;
import java.util.stream.Collectors;

public class PaymentService {

    private final PaymentDAO paymentDAO;

    public PaymentService() {
        this.paymentDAO = new PaymentDAO();
    }

    public List<Payment> getUnallocatedPayments() {

        return paymentDAO.getAllPayments()
                .stream()
                .filter(payment ->
                        "UNALLOCATED".equals(
                                payment.getStatus().name()
                        )
                )
                .collect(Collectors.toList());
    }

    public List<Payment> getCustomerPayments(String customerId) {

        return paymentDAO.getAllPayments()
                .stream()
                .filter(payment ->
                        payment.getCustomerId().equals(customerId)
                )
                .collect(Collectors.toList());
    }
}